/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.model.AuthorizationToken.PersonalAccessToken
import ch.renku.acceptancetests.model._
import ch.renku.acceptancetests.model.users.UserCredentials
import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.string.Url

import java.lang.System.getProperty

trait AcceptanceSpecData {
  self: RenkuApi =>

  private val testsDefaults = TestsDefaults()

  protected implicit lazy val renkuBaseUrl: RenkuBaseUrl = {
    val maybeEnv = sys.env.get("RENKU_TEST_URL") orElse Option(getProperty("env")) orElse testsDefaults.env
    maybeEnv flatMap as(RenkuBaseUrl.apply) getOrElse showErrorAndStop(
      "-Denv argument or RENKU_TEST_URL environment variable is not a valid URL"
    )
  }

  protected implicit lazy val gitLabBaseUrl: GitLabBaseUrl = {
    val maybeGitLabUrl = sys.env
      .get("GITLAB_TEST_URL")
      .orElse(Option(getProperty("gitLabUrl")))
      .orElse(testsDefaults.gitlaburl flatMap toNonEmpty)
    maybeGitLabUrl flatMap as(GitLabBaseUrl.apply) getOrElse showErrorAndStop(
      "-DgitLabUrl argument or GITLAB_TEST_URL environment variable is not a valid URL"
    )
  }

  protected implicit lazy val gitLabAPIUrl: GitLabApiUrl = GitLabApiUrl(gitLabBaseUrl)

  protected implicit lazy val cliVersion: CliVersion = CliVersion
    .get {
      sys.env
        .get("RENKU_CLI_VERSION")
        .orElse(Option(getProperty("cliVersion")))
        .orElse(testsDefaults.cliversion flatMap toNonEmpty)
        .getOrElse(apiCliVersion.value)
    }
    .fold(throw _, identity)

  private def as[T <: BaseUrl](factory: String Refined Url => T): String => Option[T] = url => {
    val baseUrl = if (url.endsWith("/")) url.substring(0, url.length - 1) else url
    RefType.applyRef[String Refined Url](baseUrl).map(factory).toOption
  }

  protected implicit lazy val userCredentials: UserCredentials = {
    for {
      email <- sys.env.get("RENKU_TEST_EMAIL") orElse Option(
                 getProperty("email")
               ) orElse testsDefaults.email flatMap toNonEmpty
      username <- sys.env.get("RENKU_TEST_USERNAME") orElse Option(
                    getProperty("username")
                  ) orElse testsDefaults.username flatMap toNonEmpty
      password <- sys.env.get("RENKU_TEST_PASSWORD") orElse Option(
                    getProperty("password")
                  ) orElse testsDefaults.password flatMap toNonEmpty
      fullName <- sys.env.get("RENKU_TEST_FULL_NAME") orElse Option(
                    getProperty("fullname")
                  ) orElse testsDefaults.fullname flatMap toNonEmpty
      useProvider = sys.env.get("RENKU_TEST_PROVIDER") orElse Option(getProperty("provider")) match {
                      case Some(s) => s.nonEmpty
                      case None    => false
                    }
      register = sys.env.get("RENKU_TEST_REGISTER") orElse Option(getProperty("register")) match {
                   case Some(s) => s.nonEmpty
                   case None    => false
                 }
      maybeGitLabAccessToken = sys.env
                                 .get("RENKU_TEST_GITLAB_ACCESS_TOKEN")
                                 .orElse(Option(getProperty("gitlabaccesstoken")))
                                 .orElse(testsDefaults.gitlabaccesstoken)
                                 .flatMap(toNonEmpty)
    } yield UserCredentials(email,
                            username,
                            password,
                            fullName,
                            maybeGitLabAccessToken map PersonalAccessToken.apply,
                            useProvider,
                            register
    )
  } getOrElse showErrorAndStop(
    "You must provide either the arguments -Dusername -Dfullname, -Demail or/and -Dpassword args invalid or missing" +
      " or set the environment variables RENKU_TEST_EMAIL RENKU_TEST_USERNAME RENKU_TEST_PASSWORD and/or RENKU_TEST_FULL_NAME"
  )

  private def toNonEmpty(value: String): Option[String] = value.trim match {
    case ""       => None
    case nonBlank => Some(nonBlank)
  }

  private def showErrorAndStop[T](message: String): T = {
    Console.err.println(message)
    System.exit(1)
    throw new IllegalArgumentException(message)
  }
}
