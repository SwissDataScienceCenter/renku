package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows._

import org.openqa.selenium.JavascriptExecutor;

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Run the HandsOn from the documentation.
  */
class HandsOnSpec
    extends AcceptanceSpec
    with Collaboration
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with FlightsTutorial {

  scenario("User can do hands-on tutorial") {

    implicit val loginType: LoginType = logIntoRenku

    implicit val docsScreenshots = new DocsScreenshots(this, browser)

    implicit val projectDetails: ProjectDetails =
      ProjectDetails.generateHandsOnProject(docsScreenshots.captureScreenshots)

    createNewProject

    doHandsOn
    verifyDatasetCreated
    verifyAnalysisRan

    verifyCollaborationIsEmpty
    setProjectTags
    setProjectDescription
    removeProjectInGitLab
    verifyProjectWasRemoved

    logOutOfRenku
  }

  def doHandsOn(implicit projectDetails: ProjectDetails, docsScreenshots: DocsScreenshots): Unit = {
    val projectPage = ProjectPage()
    When("user clicks on the Environments tab")
    click on projectPage.Environments.tab
    docsScreenshots.reachedCheckpoint()

    And("then they click on the New link")
    click on projectPage.Environments.newLink sleep (2 seconds)
    And("once the image is built")
    projectPage.Environments.verifyImageReady
    docsScreenshots.reachedCheckpoint()

    And("the user clicks on the Start Environment button")
    click on projectPage.Environments.startEnvironment
    Then("they should be redirected to the Environments -> Running tab")
    verify userCanSee projectPage.Environments.Running.title

    When("the environment is ready")
    projectPage.Environments.Running.verifyEnvironmentReady
    And("the user clicks on the Connect button in the table")
    // Sleep a little while after clicking to give the server a chance to come up
    click on projectPage.Environments.Running.title sleep (30 seconds)
    docsScreenshots.reachedCheckpoint()

    projectPage.Environments.Running.connectToJupyterLab

    Then("a JupyterLab page is opened on a new tab")
    val jupyterLabPage = JupyterLabPage()
    verify browserSwitchedTo jupyterLabPage sleep (5 seconds)
    docsScreenshots.reachedCheckpoint()

    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)
    followFlightsTutorial(jupyterLabPage)

    When("the user switches back to the Renku tab")
    verify browserSwitchedTo projectPage
    And("the Environments tab")
    click on projectPage.Environments.tab
    And("they turn off the interactive session they started before")
    click on projectPage.Environments.Running.connectDotButton
    click on projectPage.Environments.Running.stopButton sleep (10 seconds)
    Then("the session gets stopped and they can see the New Session link")
    verify userCanSee projectPage.Environments.newLink
  }

  def verifyDatasetCreated(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Datasets tab")
    click on projectPage.Datasets.tab
    Then("they should see the flights dataset")
    click on projectPage.Datasets.DatasetsList.flights
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
