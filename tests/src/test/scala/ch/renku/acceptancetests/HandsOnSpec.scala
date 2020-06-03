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
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with FlightsTutorialJupyter {

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
    val projectPage    = ProjectPage()
    val jupyterLabPage = launchEnvironment

    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)
    followFlightsTutorial(jupyterLabPage)

    stopEnvironment
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
