package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.language.postfixOps

trait Fork {
  self: AcceptanceSpec with RemoveProject =>

  def forkTestCase(implicit projectDetails: ProjectDetails, loginType: LoginType): Unit = {
    val projectPage = ProjectPage()
    go to projectPage
    val forkedProject = forkProject
    removeProjectInGitLab(forkedProject.projectDetails, implicitly)
    verifyProjectWasRemovedInRenku(forkedProject.projectDetails)
  }

  def forkProject(implicit projectDetails: ProjectDetails): ForkedProject = {
    val projectPage = ProjectPage()
    When("user clicks on the fork button")
    click on projectPage.forkButton

    val forkedProjectDetails: ProjectDetails = ProjectDetails.generate()

    And(s"fills in the title (${forkedProjectDetails.title}) and submits")
    projectPage.ForkDialog.submitFormWith(forkedProjectDetails)

    Then("the project gets forked and the project page gets displayed")
    val forkedProjectPage = ProjectPage()(forkedProjectDetails, implicitly)
    verify browserAt forkedProjectPage

    new ForkedProject(forkedProjectDetails)
  }

  class ForkedProject(val projectDetails: ProjectDetails) {}
}
