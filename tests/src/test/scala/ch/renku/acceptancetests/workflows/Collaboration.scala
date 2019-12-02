package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.language.postfixOps

trait Collaboration {
  self: AcceptanceSpec =>

  def verifyCollaborationIsEmpty(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab
    And("they navigate to the Merge Requests sub tab")
    click on projectPage.Collaboration.MergeRequests.tab
    Then("they should see a 'No merge requests' info")
    verify userCanSee projectPage.Collaboration.MergeRequests.noMergeRequests
  }
}
