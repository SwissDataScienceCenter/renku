package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.{ProjectDetails, ProjectIdentifier}
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnvConfig, DocsScreenshots}

import scala.concurrent.duration._
import scala.language.postfixOps

trait Environments {
  self: AcceptanceSpec =>

  def launchEnvironment(implicit projectDetails: ProjectDetails, docsScreenshots: DocsScreenshots): JupyterLabPage = {
    implicit val projectPage: ProjectPage = ProjectPage()
    When("user clicks on the Environments tab")
    click on projectPage.Environments.tab
    docsScreenshots.reachedCheckpoint()

    clickNewAndWaitForImageBuild
    docsScreenshots.reachedCheckpoint()

    clickStartEnvironmentAndWaitForReady
    docsScreenshots.reachedCheckpoint()

    projectPage.Environments.Running.connectToJupyterLab

    Then("a JupyterLab page is opened on a new tab")
    val jupyterLabPage = JupyterLabPage()
    verify browserSwitchedTo jupyterLabPage sleep (5 seconds)
    jupyterLabPage
  }

  def stopEnvironment(implicit projectDetails: ProjectDetails): Unit =
    stopEnvironmentOnProjectPage(ProjectPage())

  def stopEnvironment(projectId: ProjectIdentifier): Unit =
    stopEnvironmentOnProjectPage(ProjectPage(projectId))

  def stopEnvironmentOnProjectPage(projectPage: ProjectPage): Unit = {
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

  def launchAnonymousEnvironment(implicit anonEnvConfig: AnonEnvConfig): Option[JupyterLabPage] = {
    val projectId   = anonEnvConfig.projectId
    val projectPage = ProjectPage(projectId)
    go to projectPage
    When("user is logged out")
    And(s"goes to $projectId")
    And("clicks on the Environments tab to launch an anonymous notebook")
    click on projectPage.Environments.tab
    if (anonEnvConfig.isAvailable) {
      And("anonymous environments are supported")
      Some(anonymousEnvironmentSupported(projectPage, projectId))
    } else {
      And("anonymous environments are not supported")
      anonymousEnvironmentUnsupported(projectPage)
      None
    }
  }

  def launchUnprivilegedEnvironment(implicit anonEnvConfig: AnonEnvConfig): JupyterLabPage = {
    val projectId = anonEnvConfig.projectId
    implicit val projectPage: ProjectPage = ProjectPage(projectId)
    go to projectPage
    When(s"user goes to $projectId")
    And("clicks on the Environments tab to launch an unprivileged notebook")
    click on projectPage.Environments.tab

    clickNewAndWaitForImageBuild
    clickStartEnvironmentAndWaitForReady

    projectPage.Environments.Running.connectToJupyterLab

    Then("a JupyterLab page is opened on a new tab")
    val jupyterLabPage = JupyterLabPage(projectId)
    // TODO This does not work for some reason
    // verify browserSwitchedTo jupyterLabPage sleep (5 seconds)
    jupyterLabPage
  }

  def anonymousEnvironmentUnsupported(projectPage: ProjectPage): Option[JupyterLabPage] = {
    Then("they should see a message")
    projectPage.Environments.anonymousUnsupported
    None
  }

  def anonymousEnvironmentSupported(pp: ProjectPage, projectId: ProjectIdentifier): JupyterLabPage = {
    implicit val projectPage: ProjectPage = pp
    sleep(5 seconds)
    clickNewAndWaitForImageBuild
    clickStartEnvironmentAndWaitForReady

    projectPage.Environments.Running.connectToAnonymousJupyterLab

    Then("a JupyterLab page is opened on a new tab")
    val jupyterLabPage = JupyterLabPage(projectId)
    // TODO This does not work for some reason
    // verify browserSwitchedTo jupyterLabPage sleep (5 seconds)
    jupyterLabPage
  }

  def clickNewAndWaitForImageBuild(implicit projectPage: ProjectPage): Unit = {
    And("then they click on the New link")
    click on projectPage.Environments.newLink sleep (2 seconds)
    And("once the image is built")
    projectPage.Environments.verifyImageReady
  }

  def clickStartEnvironmentAndWaitForReady(implicit projectPage: ProjectPage): Unit = {
    And("the user clicks on the Start Environment button")
    click on projectPage.Environments.startEnvironment
    Then("they should be redirected to the Environments -> Running tab")
    verify userCanSee projectPage.Environments.Running.title

    When("the environment is ready")
    projectPage.Environments.Running.verifyEnvironmentReady
    And("the user clicks on the Connect button in the table")
    // Sleep a little while after clicking to give the server a chance to come up
    click on projectPage.Environments.Running.title sleep (30 seconds)
  }
}
