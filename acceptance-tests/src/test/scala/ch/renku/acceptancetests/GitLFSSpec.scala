package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.datasets.{DatasetName, DatasetURL}
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.ProjectPage
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows._
import eu.timepit.refined.auto._
import scala.language.postfixOps
import scala.concurrent.duration._

// Pamela to check/complete/fix
class GitLFSSpec  extends AcceptanceSpec
  with Collaboration
  with Environments
  with Login
  with NewProject
  with Datasets{

  scenario("User can start a notebook with auto-fetch LFS") {

    implicit val loginType: LoginType = logIntoRenku
    implicit val projectDetails: ProjectDetails = ProjectDetails.generate()
    implicit val projectPage: ProjectPage = ProjectPage()

    Given("a renku project with imported dataset")
    createNewProject(projectDetails)

    val importDatasetURL = DatasetURL("10.7910/DVN/WTZS4K") // to parametrize?
    val importedDatasetName = DatasetName("2019-01 US Flights")
    And("an imported dataset for this project")
    val importedDatasetPage = `import a dataset`(importDatasetURL, importedDatasetName)

    // Verify dataset was imported
    verifyAutoLFSDatasetOnJupyterNotebook

    // removeProjectInGitLab
    // verifyProjectWasRemovedInRenku

    logOutOfRenku

  }


}
