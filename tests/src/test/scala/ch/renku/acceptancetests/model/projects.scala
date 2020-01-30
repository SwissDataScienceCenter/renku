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
      description: String Refined NonEmpty,
      readmeTitle: String
  )

  object ProjectDetails {

    def generate: ProjectDetails = {
      val now         = LocalDateTime.now()
      val desc        = prefixParagraph("An automatically generated project for testing: ").generateOne
      val readmeTitle = s"test${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm_ss"))}"
      ProjectDetails(Refined.unsafeApply(s"test_${now.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmm_ss"))}"),
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
