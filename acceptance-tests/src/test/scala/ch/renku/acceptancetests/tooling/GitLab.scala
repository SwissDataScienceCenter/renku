/*
 * Copyright 2021 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.model.AuthorizationToken.OAuthAccessToken
import org.http4s.Status.Ok
import org.http4s.UrlForm
import org.scalatest.Matchers.fail

trait GitLab extends RestClient {
  self: AcceptanceSpecData =>

  lazy val oauthAccessToken: OAuthAccessToken =
    POST(gitLabBaseUrl / "oauth" / "token")
      .withEntity(
        UrlForm(
          "grant_type" -> "password",
          "username"   -> userCredentials.username.toString(),
          "password"   -> userCredentials.password.toString()
        )
      )
      .send
      .whenReceived(status = Ok)
      .bodyAsJson
      .extract(jsonRoot.`access_token`.string.getOption)
      .map(OAuthAccessToken.apply)
      .getOrElse(fail("OAuth Access Token couldn't be obtained"))
}
