package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows._
import org.openqa.selenium.{JavascriptExecutor, WebDriver}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Run the HandsOn from the documentation.
  */
class HandsOnSpec
    extends AcceptanceSpec
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with JupyterNotebook
    with FlightsTutorial
    with FlightsTutorialJupyterLab {

  scenario("User can do hands-on tutorial") {

    implicit val loginType: LoginType = logIntoRenku

    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)

    implicit val projectDetails: ProjectDetails =
      ProjectDetails.generateHandsOnProject(docsScreenshots.captureScreenshots)

    createNewProject

    followFlightsTutorial
    verifyAnalysisRan

    verifyMergeRequestsIsEmpty
    verifyIssuesIsEmpty
    createNewIssue

    setProjectTags
    setProjectDescription
    removeProjectInGitLab
    verifyProjectWasRemoved

    logOutOfRenku
  }

  def followFlightsTutorial(implicit projectDetails: ProjectDetails, docsScreenshots: DocsScreenshots): Unit = {
    // The the version that runs on the users machine
    // val projectHttpUrl     = findProjectHttpUrl
    // val flightsDatasetName = followTheFlightsTutorialOnUsersMachine(projectHttpUrl)
    // verifyDatasetCreated(flightsDatasetName)
    // verifyUserCanWorkWithJupyterNotebook

    val jupyterLabPage = launchEnvironment
    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)

    val flightsDatasetName = followFlightsTutorialInJupyterLab(jupyterLabPage)

    stopEnvironment
    verifyDatasetCreated(flightsDatasetName)
  }

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

  def verifyAnalysisRan(implicit projectDetails: ProjectDetails, docsScreenshots: DocsScreenshots): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Files tab")
    click on projectPage.Files.tab
    And("they click on the notebooks folder in the File View")
    click on (projectPage.Files.FileView folder "notebooks") sleep (2 seconds)
    And("they click on the 01-CountFlights.ran.ipynb file in the File View")
    click on (projectPage.Files.FileView file "notebooks/01-CountFlights.ran.ipynb") sleep (2 seconds)
    Then("they should see a file header with the notebook filename")
    verify that projectPage.Files.Info.heading contains "notebooks/01-CountFlights.ran.ipynb"
    And("the correct notebook content")
    // Scroll to the bottom of the page
    webDriver.asInstanceOf[JavascriptExecutor].executeScript("window.scrollBy(0,document.body.scrollHeight)")
    docsScreenshots.reachedCheckpoint()

    val resultCell = projectPage.Files.Notebook.cellWithText("There were 4951 flights to Austin, TX in Jan 2019.")
    verify that resultCell contains "There were 4951 flights to Austin, TX in Jan 2019."
  }
}
