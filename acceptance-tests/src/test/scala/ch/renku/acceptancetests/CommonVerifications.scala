package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.ProjectPage
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows.{Environments, JupyterNotebook}
import org.openqa.selenium.WebDriver

import scala.concurrent.duration._

trait CommonVerifications extends JupyterNotebook with Environments with AcceptanceSpec {

  def verifyUserCanWorkWithJupyterNotebook(implicit projectDetails: ProjectDetails,
                                           docsScreenshots:         DocsScreenshots): Unit = {
    val jupyterLabPage = launchEnvironment

    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)
    val datasetName = DatasetName.generate
    And("the user creates a dataset")
    `create a dataset`(jupyterLabPage, datasetName)

    stopEnvironment

    Then("the user can see the created dataset")
    verifyDatasetCreated(datasetName)
  }

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
