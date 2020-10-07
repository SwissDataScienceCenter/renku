package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.ProjectPage
import ch.renku.acceptancetests.tooling.AcceptanceSpec

trait BrowserNavigation {
  self: AcceptanceSpec =>

  def switchToRenkuTab(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user switches back to the Renku tab")
    verify browserSwitchedTo projectPage
  }

}
