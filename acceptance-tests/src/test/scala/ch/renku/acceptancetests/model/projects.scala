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

package ch.renku.acceptancetests.model

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators._
import ch.renku.acceptancetests.model.AuthorizationToken.{OAuthAccessToken, PersonalAccessToken}
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials

import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object projects {

  final case class ProjectIdentifier(namespace: String, slug: String) {
    def asProjectPath(implicit userCredentials: UserCredentials): String = s"$namespace/$slug"
  }

  final case class ProjectDetails(
      title:       String,
      visibility:  Visibility,
      description: String,
      template:    Template,
      readmeTitle: String
  ) {
    def asProjectIdentifier(implicit userCredentials: UserCredentials): ProjectIdentifier =
      ProjectIdentifier(
        namespace = userCredentials.userNamespace,
        slug = title.toPathSegment
      )

    def asProjectPath(implicit userCredentials: UserCredentials): String = {
      val identifier = asProjectIdentifier
      s"${identifier.namespace}/${identifier.slug}"
    }
  }

  sealed abstract class Visibility(val value: String)

  object Visibility {
    case object Public   extends Visibility(value = "public")
    case object Private  extends Visibility(value = "private")
    case object Internal extends Visibility(value = "internal")
  }

  case class Template(name: String)

  final case class ProjectUrl(value: String) {
    override lazy val toString: String = value
  }

  object ProjectUrl {

    implicit class ProjectUrlOps(projectUrl: ProjectUrl)(implicit userCredentials: UserCredentials) {

      def add(authorizationToken: AuthorizationToken): String = {
        val protocol = new URL(projectUrl.value).getProtocol
        projectUrl.value
          .replace(
            s"$protocol://",
            s"$protocol://${tokenPart(authorizationToken)}"
          )
      }

      private lazy val tokenPart: AuthorizationToken => String = {
        case OAuthAccessToken(token)    => s"oauth2:$token@"
        case PersonalAccessToken(token) => s"gitlab-ci-token:$token@"
      }
    }
  }

  object ProjectDetails {

    def generate(maybeTitle:       Option[String] = None,
                 visibility:       Visibility = Visibility.Public,
                 maybeDescription: Option[String] = None,
                 maybeTemplate:    Option[String] = None
    ): ProjectDetails = {
      val now = LocalDateTime.now()
      val title = maybeTitle.getOrElse(
        s"test ${now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))}"
      )
      val desc = maybeDescription.getOrElse(
        prefixParagraph("An automatically generated project for testing: ", maxWords = 5).generateOne
      )
      val readmeTitle = s"test ${now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))}"
      val template    = Template(maybeTemplate getOrElse "Basic Python")
      ProjectDetails(title, visibility, desc, template, readmeTitle)
    }

    implicit class TitleOps(title: String) {
      lazy val toPathSegment: String = title.replace(" ", "-")
    }
  }
}
