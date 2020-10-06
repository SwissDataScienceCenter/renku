package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.ProjectPage
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import org.openqa.selenium.WebDriver

trait Datasets {
  self: AcceptanceSpec =>

  def verifyDatasetCreated(datasetName: DatasetName)(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Datasets tab")
    click on projectPage.Datasets.tab

    And("the events are processed")
    val datasetPageTitle = projectPage.Datasets.title(_: WebDriver)
    reload whenUserCannotSee datasetPageTitle

    Then(s"the user should see a link to the '$datasetName' dataset")
    val datasetLink = projectPage.Datasets.DatasetsList.link(to = datasetName)(_: WebDriver)
    reload whenUserCannotSee datasetLink
    verify userCanSee projectPage.Datasets.DatasetsList.link(to = datasetName)
  }
}
