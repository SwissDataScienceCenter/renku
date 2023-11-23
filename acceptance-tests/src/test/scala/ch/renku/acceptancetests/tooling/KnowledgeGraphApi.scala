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
import ch.renku.acceptancetests.model.{UrlNoQueryParam, datasets, projects}
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

  def `wait for KG to process events`(projectSlug: projects.Slug, browser: WebDriver): Unit = {
    sleep(1 second)
    val gitLabProjectId = `get GitLab project id`(projectSlug)
    checkStatusAndWait(projectSlug, gitLabProjectId, browser, 1)
  }

  def `wait for project activation`(projectSlug: projects.Slug)(implicit browser: WebDriver): Either[String, Unit] = {
    sleep(1 second)
    val gitLabProjectId = `get GitLab project id`(projectSlug)
    checkActivatedAndWait(projectSlug, gitLabProjectId, browser, attempt = 1)
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

  def `PATCH /knowledge-graph/projects/:slug`(slug: projects.Slug, updates: ProjectUpdates): Unit =
    ProjectUpdates.MultipartEncoder
      .encode(updates)
      .flatMap(payload =>
        PATCH(projectUri(slug))
          .withEntity(payload)
          .putHeaders(payload.headers)
          .withAuthorizationToken(authorizationToken)
          .sendIO(expect(status = Accepted, otherwiseLog = s"Updating '$slug' project failed"))
      )
      .unsafeRunSync()

  def `GET /knowledge-graph/projects/:slug`(slug: projects.Slug): Option[KGProjectDetails] =
    GET(projectUri(slug))
      .putHeaders(`Content-Type`(application.json))
      .withAuthorizationToken(authorizationToken)
      .sendIO(mapResponseIO { response =>
        response.status match {
          case Ok       => response.as[KGProjectDetails].map(_.some)
          case NotFound => Option.empty[KGProjectDetails].pure[IO]
          case status   => fail(s"finding project details in KG returned $status")
        }
      })
      .unsafeRunSync()

  def `GET /knowledge-graph/projects/:slug/datasets`(slug: projects.Slug): List[datasets.Slug] = {
    implicit val dsSlugDecoder: Decoder[datasets.Slug] = _.downField("slug").as[String].map(datasets.Slug)
    println(projectUri(slug) / "datasets")
    GET(projectUri(slug) / "datasets")
      .putHeaders(`Content-Type`(application.json))
      .withAuthorizationToken(authorizationToken)
      .sendIO(mapResponseIO { response =>
        response.as[Json].map(println).flatMap { _ =>
          response.status match {
            case Ok       => response.as[List[datasets.Slug]]
            case NotFound => List.empty[datasets.Slug].pure[IO]
            case status   => fail(s"finding project datasets in KG returned $status")
          }
        }
      })
  }.unsafeRunSync()

  def `GET /knowledge-graph/projects/:slug/files/:path/lineage`(slug: projects.Slug, filePath: String): JsonObject =
    GET(projectUri(slug) / "files" / filePath / "lineage")
      .withAuthorizationToken(authorizationToken)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.obj.getOption)
      .getOrElse(fail(s"Cannot find lineage data for project $slug and file $filePath"))

  def `DELETE /knowledge-graph/projects/:slug`(slug: projects.Slug): Unit =
    DELETE(projectUri(slug))
      .withAuthorizationToken(authorizationToken)
      .send(expect(status = Accepted, otherwiseLog = s"Deletion of '$slug' project failed"))

  private def projectUri(slug: projects.Slug): UrlNoQueryParam = {
    val toSegments: String => List[String] = _.split('/').toList
    toSegments(slug.value).foldLeft(renkuBaseUrl / "knowledge-graph" / "projects")(_ / _)
  }

  @tailrec
  private def checkStatusAndWait(
      projectSlug:     projects.Slug,
      gitLabProjectId: Int,
      browser:         WebDriver,
      attempt:         Int
  ): Unit =
    if (attempt >= 60 * 10)
      fail(s"Events for '$projectSlug' project not processed after 10 minutes")
    else if (findTotalDone(projectSlug, gitLabProjectId, browser) == 0) {
      sleep(1 second)
      checkStatusAndWait(projectSlug, gitLabProjectId, browser, attempt + 1)
    } else if (findProgress(projectSlug, gitLabProjectId, browser) < 100d) {
      sleep(1 second)
      checkStatusAndWait(projectSlug, gitLabProjectId, browser, attempt + 1)
    } else if (findProgress(projectSlug, gitLabProjectId, browser) == 100d) {
      val maybeDetails = findStatus(projectSlug, gitLabProjectId, browser).flatMap(_.maybeDetails)
      maybeDetails match {
        case Some(status) if status.status == "failure" =>
          val stackTrace = status.maybeStackTrace.map(s => s"; stackTrace:\n${s.replace("; ", "; \n")}").getOrElse("")
          fail(s"Project $projectSlug ($gitLabProjectId) failed with '${status.message}'$stackTrace")
        case _ => ()
      }
    }

  @tailrec
  private def checkActivatedAndWait(
      projectSlug:     projects.Slug,
      gitLabProjectId: Int,
      browser:         WebDriver,
      attempt:         Int,
      startTime:       Instant = Instant.now()
  ): Either[String, Unit] = {
    logger.info(s"waits for Project Activation - attempt $attempt")
    if (attempt >= 60) {
      val duration = Duration(Instant.now().toEpochMilli - startTime.toEpochMilli, MILLISECONDS).toSeconds
      s"Activation status of '$projectSlug' project couldn't be determined after ${duration}s".asLeft[Unit]
    } else if (!checkActivated(projectSlug, gitLabProjectId, browser)) {
      sleep(1 second)
      checkActivatedAndWait(projectSlug, gitLabProjectId, browser, attempt + 1, startTime)
    } else ().asRight[String]
  }

  private def checkActivated(projectSlug: projects.Slug, gitLabProjectId: Int, browser: WebDriver): Boolean =
    findStatus(projectSlug, gitLabProjectId, browser).exists(_.activated)

  private def findProgress(projectSlug: projects.Slug, gitLabProjectId: Int, browser: WebDriver): Double =
    findStatus(projectSlug, gitLabProjectId, browser).map(_.progressPercentage).getOrElse(0d)

  private def findTotalDone(projectSlug: projects.Slug, gitLabProjectId: Int, browser: WebDriver): Int =
    findStatus(projectSlug, gitLabProjectId, browser).map(_.total).getOrElse(0)

  private def findStatus(projectSlug: projects.Slug, gitLabProjectId: Int, browser: WebDriver): Option[GraphStatus] =
    GET(renkuBaseUrl / "api" / "projects" / gitLabProjectId / "graph" / "status")
      .addCookiesFrom(browser)
      .send { response =>
        response.status match {
          case Ok       => response.as[GraphStatus].map(_.some)
          case NotFound => None.pure[IO]
          case other =>
            IO(logger.warn(s"Finding processing status for '$projectSlug' returned $other")).as(None)
        }
      }
}
