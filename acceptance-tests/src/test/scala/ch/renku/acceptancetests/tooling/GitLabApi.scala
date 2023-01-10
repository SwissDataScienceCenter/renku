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

import cats.syntax.all._
import ch.renku.acceptancetests.model.AuthorizationToken
import ch.renku.acceptancetests.model.AuthorizationToken.OAuthAccessToken
import ch.renku.acceptancetests.model.projects.ProjectIdentifier
import org.http4s.Status._
import org.http4s.UrlForm
import org.scalatest.Assertions.fail

trait GitLabApi extends RestClient {
  self: AcceptanceSpecData with IOSpec =>

  lazy val authorizationToken: AuthorizationToken =
    userCredentials.maybeGitLabAccessToken getOrElse oauthAccessToken

  private lazy val oauthAccessToken: OAuthAccessToken =
    fetchOAuthTokenRequest
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.`access_token`.string.getOption)
      .map(OAuthAccessToken.apply)
      .getOrElse(fail("OAuth Access Token couldn't be obtained"))

  def `check user has credentials`: Boolean =
    fetchOAuthTokenRequest
      .send(mapResponse {
        _.status match {
          case Ok         => true
          case BadRequest => false
          case _          => fail(s"Cannot determine if user '${userCredentials.username}' has credentials in GitLab")
        }
      })

  private def fetchOAuthTokenRequest =
    POST(gitLabBaseUrl / "oauth" / "token")
      .withEntity(
        UrlForm(
          "grant_type" -> "password",
          "username"   -> userCredentials.username,
          "password"   -> userCredentials.password
        )
      )

  def `get GitLab project id`(projectId: ProjectIdentifier): Int =
    GET(gitLabAPIUrl / "projects" / projectId.asProjectPath)
      .withAuthorizationToken(authorizationToken)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.id.int.getOption)
      .getOrElse(fail(s"Cannot find '$projectId' project in GitLab"))

  def `project exists in GitLab`(projectId: ProjectIdentifier): Boolean =
    GET(gitLabAPIUrl / "projects" / projectId.asProjectPath)
      .withAuthorizationToken(authorizationToken)
      .send(mapResponse {
        _.status match {
          case Ok           => true
          case NotFound     => false
          case Unauthorized => false
          case other        => fail(s"Finding '${projectId.slug}' project in GitLab failed with $other status")
        }
      })

  def `delete project in GitLab`(projectId: ProjectIdentifier): Unit =
    DELETE(gitLabAPIUrl / "projects" / projectId.asProjectPath)
      .withAuthorizationToken(authorizationToken)
      .send(expect(status = Accepted, otherwiseLog = s"Deletion of '${projectId.slug}' project in GitLab failed"))
}
