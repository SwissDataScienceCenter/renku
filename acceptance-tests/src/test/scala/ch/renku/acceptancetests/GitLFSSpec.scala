package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows._

// Pamela to check/complete/fix
class GitLFSSpec  extends AcceptanceSpec
  with Collaboration
  with Environments
  with Login
  with NewProject{

  scenario("User can start a notebook with auto-fetch LFS") {

    implicit val loginType: LoginType = logIntoRenku
    implicit val projectDetails: ProjectDetails = ProjectDetails.generate()

    Given("a new renku project")
    createNewProject(projectDetails)

    And("an imported dataset for this project")
    val originalDatasetPage = `import a dataset`(datasetName)


  }

  def verifyUserCanSeeDataInJupyterNotebook(implicit
                                           projectDetails:  ProjectDetails,
                                           docsScreenshots: DocsScreenshots
                                          ): Unit = {
    val jupyterLabPage = launchEnvironment

    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)
    val datasetName = DatasetName.generate
    And("the user creates a dataset")
    `create a dataset`(jupyterLabPage, datasetName)

    stopEnvironment

    Then("the user can see the imported dataset")
    verifyDatasetCreated(datasetName)
  }

}
