package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages.ProjectPage

import scala.language.postfixOps

class MinimalFunctionalitySpec
    extends AcceptanceSpec
    with Collaboration
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with Fork {

  scenario("User can use basic functionality of Renku") {

    implicit val loginType: LoginType = logIntoRenku

    implicit val projectDetails: ProjectDetails = ProjectDetails.generate

    createNewProject

    verifyCollaborationIsEmpty
    setProjectTags
    setProjectDescription

    forkTestCase

    go to ProjectPage()
    removeProjectInGitLab
    verifyProjectWasRemoved

    logOutOfRenku
  }
}
