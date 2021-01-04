package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

import ch.renku.acceptancetests.model.projects.ProjectIdentifier
import eu.timepit.refined.api.Refined

case class AnonEnvConfig(projectId: ProjectIdentifier, isAvailable: Boolean = false)

/**
  * Configuration for the anonymous environment
  */
trait AnonEnv extends AcceptanceSpecData {

  protected implicit lazy val anonEnvConfig: AnonEnvConfig =
    AnonEnvConfig(anonProjectIdentifier, isAnonEnvAvailable)

  protected lazy val anonProjectIdentifier: ProjectIdentifier = {
    Option(getProperty("anon")) orElse sys.env.get("RENKU_TEST_ANON_PROJECT") match {
      case Some(s) =>
        val projectId = s.split("/").map(_.trim)
        ProjectIdentifier(Refined.unsafeApply(projectId(0)), Refined.unsafeApply(projectId(1)))
      case None =>
        ProjectIdentifier(Refined.unsafeApply("andi"), Refined.unsafeApply("public-test-project"))
    }
  }

  protected lazy val isAnonEnvAvailable: Boolean = {
    val baseUrl = renkuBaseUrl
    Option(getProperty("anonAvail")) orElse sys.env.get("RENKU_TEST_ANON_AVAILABLE") match {
      case Some(s) =>
        s.toLowerCase == "true"
      case None =>
        baseUrl.value.value.equals("https://dev.renku.ch")
    }
  }

}
