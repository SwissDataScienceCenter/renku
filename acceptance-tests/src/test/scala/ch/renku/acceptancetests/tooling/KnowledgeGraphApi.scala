/*
 * Copyright 2023 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.tooling

import KnowledgeGraphModel._
import TestLogger.logger
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.model.projects.ProjectIdentifier
import io.circe._
import org.http4s.MediaType._
import org.http4s.Status.{Accepted, Created, NotFound, Ok}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.headers.`Content-Type`
import org.openqa.selenium.WebDriver
import org.scalatest.matchers.should

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration._

trait KnowledgeGraphApi extends RestClient {
  self: AcceptanceSpecData with GitLabApi with Grammar with should.Matchers =>

  implicit val ioRuntime: IORuntime

  def `wait for KG to process events`(projectId: ProjectIdentifier, browser: WebDriver): Unit = {
    sleep(1 second)
    val gitLabProjectId = `get GitLab project id`(projectId)
    checkStatusAndWait(projectId, gitLabProjectId, browser, 1)
  }

  def `wait for project activation`(projectId: ProjectIdentifier)(implicit browser: WebDriver): Either[String, Unit] = {
    sleep(1 second)
    val gitLabProjectId = `get GitLab project id`(projectId)
    checkActivatedAndWait(projectId, gitLabProjectId, browser, attempt = 1)
  }

  def findLineage(slug: projects.Slug, filePath: String): JsonObject = {
    val toSegments: String => List[String] = _.split('/').toList
    val uri =
      toSegments(slug.value)
        .foldLeft(renkuBaseUrl / "knowledge-graph" / "projects")(_ / _) / "files" / filePath / "lineage"
    GET(uri)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.obj.getOption)
      .getOrElse(fail(s"Cannot find lineage data for project $slug file $filePath"))
  }

  def `POST /knowledge-graph/projects`(project: NewProject): projects.Slug =
    NewProject.MultipartEncoder
      .encode(project)
      .flatMap(payload =>
        POST(renkuBaseUrl / "knowledge-graph" / "projects")
          .withEntity(payload)
          .putHeaders(payload.headers)
          .withAuthorizationToken(authorizationToken)
          .sendIO(whenReceived(status = Created) >=> decodePayload(_.downField("slug").as[String].map(projects.Slug)))
      )
      .unsafeRunSync()

  def `PATCH /knowledge-graph/projects/:slug`(slug: projects.Slug, updates: ProjectUpdates): Unit = {
    val toSegments: String => List[String] = _.split('/').toList
    val uri = toSegments(slug.value).foldLeft(renkuBaseUrl / "knowledge-graph" / "projects")(_ / _)
    ProjectUpdates.MultipartEncoder
      .encode(updates)
      .flatMap(payload =>
        PATCH(uri)
          .withEntity(payload)
          .putHeaders(payload.headers)
          .withAuthorizationToken(authorizationToken)
          .sendIO(expect(status = Accepted, otherwiseLog = s"Updating '$slug' project failed"))
      )
      .unsafeRunSync()
  }

  def `GET /knowledge-graph/projects/:slug`(slug: projects.Slug): Option[KGProjectDetails] = {
    val toSegments: String => List[String] = _.split('/').toList
    val uri = toSegments(slug.value).foldLeft(renkuBaseUrl / "knowledge-graph" / "projects")(_ / _)
    GET(uri)
      .putHeaders(`Content-Type`(application.json))
      .withAuthorizationToken(authorizationToken)
      .sendIO(mapResponseIO { response =>
        response.status match {
          case Ok       => response.as[KGProjectDetails].map(_.some)
          case NotFound => Option.empty[KGProjectDetails].pure[IO]
          case status   => fail(s"finding project details in the KG returned $status")
        }
      })
  }.unsafeRunSync()

  def `DELETE /knowledge-graph/projects/:slug`(slug: projects.Slug): Unit = {
    val toSegments: String => List[String] = _.split('/').toList
    val uri = toSegments(slug.value).foldLeft(renkuBaseUrl / "knowledge-graph" / "projects")(_ / _)
    DELETE(uri)
      .withAuthorizationToken(authorizationToken)
      .send(expect(status = Accepted, otherwiseLog = s"Deletion of '$slug' project failed"))
  }

  @tailrec
  private def checkStatusAndWait(
      projectId:       ProjectIdentifier,
      gitLabProjectId: Int,
      browser:         WebDriver,
      attempt:         Int
  ): Unit =
    if (attempt >= 60 * 10)
      fail(s"Events for '$projectId' project not processed after 10 minutes")
    else if (findTotalDone(projectId, gitLabProjectId, browser) == 0) {
      sleep(1 second)
      checkStatusAndWait(projectId, gitLabProjectId, browser, attempt + 1)
    } else if (findProgress(projectId, gitLabProjectId, browser) < 100d) {
      sleep(1 second)
      checkStatusAndWait(projectId, gitLabProjectId, browser, attempt + 1)
    } else if (findProgress(projectId, gitLabProjectId, browser) == 100d) {
      val maybeDetails = findStatus(projectId, gitLabProjectId, browser).flatMap(_.maybeDetails)
      maybeDetails match {
        case Some(status) if status.status == "failure" =>
          val stackTrace = status.maybeStackTrace.map(s => s"; stackTrace:\n${s.replace("; ", "; \n")}").getOrElse("")
          fail(s"Project $projectId ($gitLabProjectId) failed with '${status.message}'$stackTrace")
        case _ => ()
      }
    }

  @tailrec
  private def checkActivatedAndWait(
      projectId:       ProjectIdentifier,
      gitLabProjectId: Int,
      browser:         WebDriver,
      attempt:         Int,
      startTime:       Instant = Instant.now()
  ): Either[String, Unit] = {
    logger.info(s"waits for Project Activation - attempt $attempt")
    if (attempt >= 60) {
      val duration = Duration(Instant.now().toEpochMilli - startTime.toEpochMilli, MILLISECONDS).toSeconds
      s"Activation status of '$projectId' project couldn't be determined after ${duration}s".asLeft[Unit]
    } else if (!checkActivated(projectId, gitLabProjectId, browser)) {
      sleep(1 second)
      checkActivatedAndWait(projectId, gitLabProjectId, browser, attempt + 1, startTime)
    } else ().asRight[String]
  }

  private def checkActivated(projectId: ProjectIdentifier, gitLabProjectId: Int, browser: WebDriver): Boolean =
    findStatus(projectId, gitLabProjectId, browser).exists(_.activated)

  private def findProgress(projectId: ProjectIdentifier, gitLabProjectId: Int, browser: WebDriver): Double =
    findStatus(projectId, gitLabProjectId, browser).map(_.progressPercentage).getOrElse(0d)

  private def findTotalDone(projectId: ProjectIdentifier, gitLabProjectId: Int, browser: WebDriver): Int =
    findStatus(projectId, gitLabProjectId, browser).map(_.total).getOrElse(0)

  private def findStatus(projectId: ProjectIdentifier, gitLabProjectId: Int, browser: WebDriver): Option[GraphStatus] =
    GET(renkuBaseUrl / "api" / "projects" / gitLabProjectId / "graph" / "status")
      .addCookiesFrom(browser)
      .send { response =>
        response.status match {
          case Ok       => response.as[GraphStatus].map(_.some)
          case NotFound => None.pure[IO]
          case other =>
            IO(logger.warn(s"Finding processing status for '$projectId' returned $other")).as(None)
        }
      }
}
