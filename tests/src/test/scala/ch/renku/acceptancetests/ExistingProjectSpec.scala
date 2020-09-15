package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.{ProjectDetails, ProjectIdentifier}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnv, ExistingProjectSpecData}
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages._

import scala.language.postfixOps

/**
  * Run tests on an existing project.
  */
class ExistingProjectSpec
    extends AcceptanceSpec
    with AnonEnv
    with Environments
    with ExistingProjectSpecData
    with Collaboration
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with Fork {

  scenario("User can list datasets") {
    existingProjectDetails match {
      case Some(pd) => {
        launchAnonymousEnvironment map (_ => stopEnvironment(anonEnvConfig.projectId))
        implicit val loginType: LoginType = logIntoRenku
        launchUnprivilegedEnvironment
        stopEnvironment(anonEnvConfig.projectId)
        logOutOfRenku
      }
      case None => {
        Given("no existing project to list datasets")
        Then("do not test anything")
      }
    }
  }

}
