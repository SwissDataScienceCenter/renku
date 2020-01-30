package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

import ch.renku.acceptancetests.model.projects.ProjectDetails
import eu.timepit.refined.api.Refined

/**
  * Configuration specific to the HandsOn spec.
  */
trait ExistingProjectSpecData {

  protected lazy val existingProjectDetails: Option[ProjectDetails] = {
    Option(getProperty("extant")) orElse sys.env.get("RENKU_TEST_EXTANT_PROJECT") match {
      case Some(s) => Some(ProjectDetails(Refined.unsafeApply(s), Refined.unsafeApply("unused"), s))
      case None    => None
    }
  }
}
