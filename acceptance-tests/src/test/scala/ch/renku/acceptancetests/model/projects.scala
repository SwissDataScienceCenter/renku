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
      visibility:  Visibility,
      description: String Refined NonEmpty,
      template:    Template,
      readmeTitle: String
  )

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
