package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows.{Collaboration, Environments, FlightsTutorial, JupyterNotebook, Login, LoginType, NewProject, RemoveProject, Settings}

class PrivateProjectSpec
    extends AcceptanceSpec
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with JupyterNotebook
    with FlightsTutorial
    with CommonVerifications {

  scenario("User can launch Jupyter notebook when the project is private") {
    implicit val loginType:       LoginType       = logIntoRenku
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)
    implicit val projectDetails: ProjectDetails =
      ProjectDetails.generateHandsOnProject(docsScreenshots.captureScreenshots)

    createNewProject
    val projectHttpUrl     = findProjectHttpUrl
    val flightsDatasetName = followTheFlightsTutorialOnUsersMachine(projectHttpUrl)
    verifyDatasetCreated(flightsDatasetName)

    verifyUserCanWorkWithJupyterNotebook
  }
}
