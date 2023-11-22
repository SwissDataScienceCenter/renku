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

import TestLogger.logger
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import ch.renku.acceptancetests.model.AuthorizationToken.OAuthAccessToken
import ch.renku.acceptancetests.model.projects.{ProjectIdentifier, ProjectUrl}
import ch.renku.acceptancetests.model.{AuthorizationToken, projects}
import io.circe.Decoder._
import io.circe.{Decoder, Json}
import org.http4s.Status._
import org.http4s.Uri.Path.SegmentEncoder._
import org.http4s.UrlForm
import org.scalatest.matchers.should

import java.lang.Thread.sleep
import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration._

trait GitLabApi extends RestClient {
  self: AcceptanceSpecData with should.Matchers =>

  implicit val ioRuntime: IORuntime

  lazy val authorizationToken: AuthorizationToken =
    userCredentials.maybeGitLabAccessToken getOrElse oauthAccessToken

  private lazy val oauthAccessToken: OAuthAccessToken =
    fetchOAuthTokenRequest
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.`access_token`.string.getOption)
      .map(OAuthAccessToken.apply)
      .getOrElse(fail("OAuth Access Token couldn't be obtained"))

  def `check user has credentials`: Boolean =
    fetchOAuthTokenRequest.send {
      mapResponse {
        _.status match {
          case Ok         => true
          case BadRequest => false
          case _          => fail(s"Cannot determine if user '${userCredentials.username}' has credentials in GitLab")
        }
      }
    }

  private def fetchOAuthTokenRequest =
    POST(gitLabBaseUrl / "oauth" / "token")
      .withEntity(
        UrlForm(
          "grant_type" -> "password",
          "username"   -> userCredentials.username,
          "password"   -> userCredentials.password
        )
      )

  def fetchUserId: Int =
    GET(gitLabAPIUrl / "user")
      .withAuthorizationToken(authorizationToken)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.id.int.getOption)
      .getOrElse(fail(s"Cannot find user id"))

  case class ProjectInfo(id: Int, path: String, fullPath: String, created: Instant)

  def `get user's projects from GitLab`: List[ProjectInfo] =
    fetchAllUserProjects(fetchUserId, page = 1, List.empty[ProjectInfo])

  @tailrec
  private def fetchAllUserProjects(userId: Int, page: Int, results: List[ProjectInfo]): List[ProjectInfo] =
    fetchPageOfUserProjects(userId, page) match {
      case Nil  => results
      case list => fetchAllUserProjects(userId, page + 1, results ::: list)
    }

  private def fetchPageOfUserProjects(userId: Int, page: Int): List[ProjectInfo] = {

    implicit val singleRowDecoder: Decoder[ProjectInfo] = cur =>
      (cur.downField("id").as[Int],
       cur.downField("path").as[String],
       cur.downField("path_with_namespace").as[String],
       cur.downField("created_at").as[Instant]
      ).mapN(ProjectInfo.apply)

    val decoder: Json => List[ProjectInfo] =
      _.as[List[ProjectInfo]].fold(_ => List.empty, identity)

    val url = (gitLabAPIUrl / "users" / userId / "projects")
      .param("per_page", "100")
      .and("order_by", "created_at")
      .and("sort", "desc")
      .and("page", page)
    GET(url)
      .withAuthorizationToken(authorizationToken)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(decoder)
  }

  def `get GitLab project id`(projectSlug: projects.Slug): Int =
    GET(gitLabAPIUrl / "projects" / projectSlug.value)
      .withAuthorizationToken(authorizationToken)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.id.int.getOption)
      .getOrElse(fail(s"Cannot find '$projectSlug' project in GitLab"))

  def `get repo Http URL`(projectSlug: projects.Slug): projects.ProjectUrl =
    GET(gitLabAPIUrl / "projects" / projectSlug.value)
      .withAuthorizationToken(authorizationToken)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.http_url_to_repo.string.getOption)
      .map(projects.ProjectUrl(_))
      .getOrElse(fail(s"Cannot repo Http URL for '$projectSlug' in GitLab"))

  def `project exists in GitLab`(projectId: ProjectIdentifier): Boolean =
    GET(gitLabAPIUrl / "projects" / projectId.asProjectSlug.value)
      .withAuthorizationToken(authorizationToken)
      .send(mapResponse {
        _.status match {
          case Ok           => true
          case NotFound     => false
          case Unauthorized => false
          case other        => fail(s"Finding '${projectId.path}' project in GitLab failed with $other status")
        }
      })

  def `wait for project creation`(projectId: ProjectIdentifier, attempt: Int = 1): Unit = {
    val waitTime = 1 second

    if (!`project exists in GitLab`(projectId) && attempt < 120) {
      logger.info("waits for Project creation in GitLab")
      sleep(waitTime.toMillis)
      `wait for project creation`(projectId)
    } else if (!`project exists in GitLab`(projectId)) {
      fail(s"project did not get created in GitLab after ${(waitTime * attempt).toSeconds} seconds")
    }
  }

  def `delete project in GitLab`(projectId: ProjectIdentifier): Unit =
    DELETE(gitLabAPIUrl / "projects" / projectId.asProjectSlug.value)
      .withAuthorizationToken(authorizationToken)
      .send(expect(status = Accepted, otherwiseLog = s"Deletion of '${projectId.path}' project in GitLab failed"))

  def `find user namespace ids`: List[projects.NamespaceId] = {
    implicit val decoder: Decoder[projects.NamespaceId] =
      Decoder.instance[projects.NamespaceId](_.downField("id").as[Int].map(projects.NamespaceId(_)))

    GET(gitLabAPIUrl / "namespaces")
      .withAuthorizationToken(authorizationToken)
      .sendIO(whenReceived(status = Ok) >=> decodePayload[List[projects.NamespaceId]])
      .unsafeRunSync()
  }
}
