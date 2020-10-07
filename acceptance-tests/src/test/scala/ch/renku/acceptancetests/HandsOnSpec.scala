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
    with Datasets {

  scenario("User can do hands-on tutorial") {

    implicit val loginType: LoginType = logIntoRenku

    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)

    implicit val projectDetails: ProjectDetails =
      ProjectDetails.generateHandsOnProject(docsScreenshots.captureScreenshots)

    createNewProject(projectDetails)

    val projectHttpUrl     = findProjectHttpUrl
    val flightsDatasetName = followTheFlightsTutorialOnUsersMachine(projectHttpUrl)
    verifyDatasetCreated(flightsDatasetName)

    verifyUserCanWorkWithJupyterNotebook

    verifyAnalysisRan

    verifyMergeRequestsIsEmpty
    verifyIssuesIsEmpty
    createNewIssue

    setProjectTags
    setProjectDescription
    removeProjectInGitLab
    verifyProjectWasRemovedInRenku

    logOutOfRenku
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
