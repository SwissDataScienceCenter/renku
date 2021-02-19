package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnv, DocsScreenshots}
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages.ProjectPage

import scala.concurrent.duration._
import scala.language.postfixOps

class MinimalFunctionalitySpec
    extends AcceptanceSpec
    with AnonEnv
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with Fork {

  scenario("User can use basic functionality of Renku") {

    implicit val loginType: LoginType = `log in to Renku`

    implicit val projectDetails: ProjectDetails = ProjectDetails.generate()

    createNewProject(projectDetails)

    verifyMergeRequestsIsEmpty
    verifyIssuesIsEmpty
    createNewIssue

    addChangeToProject
    createNewMergeRequest

    setProjectTags
    setProjectDescription

    forkTestCase

    go to ProjectPage()
    `remove project in GitLab`(projectDetails)
    `verify project is removed`

    launchUnprivilegedEnvironment
    stopEnvironment(anonEnvConfig.projectId)

    `log out of Renku`

    launchAnonymousEnvironment map (_ => stopEnvironment(anonEnvConfig.projectId))

  }

  def addChangeToProject(implicit projectDetails: ProjectDetails): Unit = {
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser) {
      override lazy val captureScreenshots: Boolean = false
    }
    val jupyterLabPage = launchEnvironment

    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)
    createBranchInJupyterLab(jupyterLabPage)

    stopEnvironment
  }
}
