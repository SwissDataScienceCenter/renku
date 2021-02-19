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

import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators._
import ch.renku.acceptancetests.model.users.UserCredentials
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import ch.renku.acceptancetests.model.projects.ProjectDetails._

object projects {

  final case class ProjectIdentifier(
      namespace: String Refined NonEmpty,
      slug:      String Refined NonEmpty
  ) {
    def asProjectPath(implicit userCredentials: UserCredentials): String =
      s"$namespace/$slug"
  }

  object ProjectIdentifier {
    def unsafeApply(namespace: String, slug: String): ProjectIdentifier =
      ProjectIdentifier(Refined.unsafeApply(namespace), Refined.unsafeApply(slug))
  }

  final case class ProjectDetails(
      title:       String Refined NonEmpty,
      visibility:  Visibility,
      description: String Refined NonEmpty,
      template:    Template,
      readmeTitle: String
  ) {
    def asProjectIdentifier(implicit userCredentials: UserCredentials): ProjectIdentifier =
      ProjectIdentifier(
        namespace = Refined.unsafeApply(userCredentials.userNamespace),
        slug = Refined.unsafeApply(title.toPathSegment)
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

  case class Template(name: String Refined NonEmpty)

  final case class ProjectUrl(value: String) {
    override lazy val toString: String = value
  }

  object ProjectUrl {

    implicit class ProjectUrlOps(projectUrl: ProjectUrl)(implicit userCredentials: UserCredentials) {
      import ch.renku.acceptancetests.tooling.UrlEncoder.urlEncode

      lazy val addGitCredentials: String = {
        val protocol = new URL(projectUrl.value).getProtocol
        projectUrl.value
          .replace(
            s"$protocol://",
            s"$protocol://${urlEncode(userCredentials.username.value)}:${urlEncode(userCredentials.password.value)}@"
          )
      }
    }
  }

  object ProjectDetails {

    def generate(maybeTitle:       Option[String Refined NonEmpty] = None,
                 visibility:       Visibility = Visibility.Public,
                 maybeDescription: Option[String Refined NonEmpty] = None,
                 maybeTemplate:    Option[String Refined NonEmpty] = None
    ): ProjectDetails = {
      val now = LocalDateTime.now()
      val title = maybeTitle.getOrElse(
        Refined.unsafeApply(s"test ${now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))}")
      )
      val desc =
        maybeDescription.getOrElse(prefixParagraph("An automatically generated project for testing: ").generateOne)
      val readmeTitle = s"test ${now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))}"
      val template    = Template(maybeTemplate.getOrElse(Refined.unsafeApply("Renku/python-minimal")))
      ProjectDetails(title, visibility, desc, template, readmeTitle)
    }

    def generateHandsOnProject(captureScreenshots: Boolean): ProjectDetails =
      if (captureScreenshots) {
        val readmeTitle = "flights tutorial"
        ProjectDetails(
          Refined.unsafeApply(readmeTitle),
          Visibility.Public,
          Refined.unsafeApply("A renku tutorial project."),
          Template(Refined.unsafeApply("This template isn't used")),
          readmeTitle
        )
      } else
        generate()

    def generatePrivateProject: ProjectDetails = generate(visibility = Visibility.Private)

    implicit class TitleOps(title: String Refined NonEmpty) {
      lazy val toPathSegment: String = title.value.replace(" ", "-")
    }
  }
}
