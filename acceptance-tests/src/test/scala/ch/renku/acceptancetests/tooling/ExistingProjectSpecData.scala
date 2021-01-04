package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Template, Visibility}
import eu.timepit.refined.api.Refined

/**
  * Configuration specific to the ExistingProject spec.
  */
trait ExistingProjectSpecData {

  protected lazy val existingProjectDetails: Option[ProjectDetails] = {
    Option(getProperty("extant")) orElse sys.env.get("RENKU_TEST_EXTANT_PROJECT") match {
      case Some(readMeTitle) =>
        Some(
          ProjectDetails(Refined.unsafeApply(readMeTitle),
                         Visibility.Public,
                         Refined.unsafeApply("unused"),
                         Template(Refined.unsafeApply("Not used")),
                         readMeTitle
          )
        )
      case None => None
    }
  }
}
