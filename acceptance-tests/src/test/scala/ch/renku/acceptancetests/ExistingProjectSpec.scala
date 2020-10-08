package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, ExistingProjectSpecData}
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages._

import scala.language.postfixOps

/**
  * Run tests on an existing project.
  */
class ExistingProjectSpec
    extends AcceptanceSpec
    with ExistingProjectSpecData
    with Collaboration
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with Fork {

  scenario("User can fork a public project") {
    existingProjectDetails match {
      case Some(projectDetails) =>
        implicit val loginType: LoginType = logIntoRenku
        Given("an existing project to fork")
        forkTestCase(projectDetails, loginType)
        logOutOfRenku
      case None =>
        Given("no existing project to fork")
        Then("do not test anything")
    }
  }

}
