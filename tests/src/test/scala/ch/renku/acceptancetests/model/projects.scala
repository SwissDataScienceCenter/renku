package ch.renku.acceptancetests.model

import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators._
import ch.renku.acceptancetests.model.users.UserCredentials
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty

object projects {

  final case class ProjectIdentifier(
      namespace: String Refined NonEmpty,
      slug:      String Refined NonEmpty
  )

  final case class ProjectDetails(
      title:       String Refined NonEmpty,
      description: String Refined NonEmpty,
      readmeTitle: String
  )

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

    def generate: ProjectDetails = {
      val now         = LocalDateTime.now()
      val desc        = prefixParagraph("An automatically generated project for testing: ").generateOne
      val readmeTitle = s"test ${now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))}"
      val template    = "Renku/python-minimal"
      ProjectDetails(Refined.unsafeApply(s"test ${now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))}"),
                     desc,
                     readmeTitle)
    }

    def generateHandsOnProject(captureScreenshots: Boolean): ProjectDetails =
      if (captureScreenshots) {
        val readmeTitle = "flights tutorial"
        ProjectDetails(Refined.unsafeApply(readmeTitle), Refined.unsafeApply("A renku tutorial project."), readmeTitle)
      } else
        generate

    implicit class TitleOps(title: String Refined NonEmpty) {
      lazy val toPathSegment: String = title.value.replace(" ", "-")
    }

  }

}
