package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.datasets.{DatasetDir, DatasetName, DatasetURL}
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.{DatasetPage, ProjectPage}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows._
import eu.timepit.refined.auto._
import org.openqa.selenium.WebDriver

import scala.language.postfixOps
import scala.concurrent.duration._

class GitLFSSpec  extends AcceptanceSpec
  with Collaboration
  with Environments
  with Login
  with NewProject
  with RemoveProject
  with Datasets{

  scenario("User can start a notebook with auto-fetch LFS") {

    implicit val loginType: LoginType = logIntoRenku
    implicit val projectDetails: ProjectDetails = ProjectDetails.generate()
    implicit val projectPage: ProjectPage = ProjectPage()
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)

    Given("a renku project with imported dataset")
    createNewProject(projectDetails)

    val importDatasetURL = DatasetURL("10.7910/DVN/WTZS4K") // to parametrize?
    val importedDatasetName = DatasetName("2019-01 US Flights")
    And("an imported dataset for this project")
    val importedDatasetPage = `import a dataset`(importDatasetURL, importedDatasetName)
    val test = importedDatasetPage.path.value
    val datasetPage = DatasetPage(importedDatasetName)
    val datasetLink = projectPage.Datasets.DatasetsList.link(to = datasetPage)(_: WebDriver)

    val importedDatasetDirectory = DatasetDir("201901_us_flights_10/2019-01-flights.csv.zip")
    verifyAutoLFSDatasetOnJupyterNotebook(projectDetails, docsScreenshots, importedDatasetDirectory)

    removeProjectInGitLab
    verifyProjectWasRemovedInRenku

    logOutOfRenku

  }


}
