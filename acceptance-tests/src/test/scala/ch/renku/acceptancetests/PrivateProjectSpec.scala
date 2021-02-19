package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Visibility}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows.{BrowserNavigation, Collaboration, Datasets, Environments, JupyterNotebook, Login, LoginType, NewProject, RemoveProject, Settings}

class PrivateProjectSpec
    extends AcceptanceSpec
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with JupyterNotebook
    with Datasets
    with BrowserNavigation {

  scenario("User can launch Jupyter notebook when the project is private") {
    implicit val loginType:       LoginType       = `log in to Renku`
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)
    implicit val projectDetails:  ProjectDetails  = ProjectDetails.generate(visibility = Visibility.Private)

    createNewProject(projectDetails)
    verifyUserCanWorkWithJupyterNotebook
    `remove project in GitLab`(projectDetails)
    switchToRenkuTab
    `log out of Renku`
  }
}
