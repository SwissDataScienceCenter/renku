package ch.renku.acceptancetests.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty

object projects {

  final case class ProjectDetails(
      title:       String Refined NonEmpty,
      description: String Refined NonEmpty
  )

  object ProjectDetails {

    def generate: ProjectDetails = ProjectDetails(
      title = Refined.unsafeApply(
        s"test ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))}"
      ),
      description = prefixParagraph("An automatically generated project for testing: ").generateOne
    )

    def generateHandsOnProject(captureScreenshots: Boolean): ProjectDetails = ProjectDetails(
      title =
        if (captureScreenshots)
          Refined.unsafeApply("flights tutorial")
        else
          Refined.unsafeApply(s"test ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))}"),
      description =
        if (captureScreenshots)
          Refined.unsafeApply("A renku tutorial project.")
        else
          prefixParagraph("An automatically generated project for testing: ").generateOne
    )

    implicit class TitleOps(title: String Refined NonEmpty) {
      lazy val toPathSegment: String = title.value.replace(" ", "-")
    }
  }
}
