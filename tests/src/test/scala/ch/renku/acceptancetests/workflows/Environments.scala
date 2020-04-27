package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}

import scala.concurrent.duration._
import scala.language.postfixOps

trait Environments {
  self: AcceptanceSpec =>

  def launchEnvironment(implicit projectDetails: ProjectDetails, docsScreenshots: DocsScreenshots): JupyterLabPage = {
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
    return jupyterLabPage
  }

  def stopEnvironment(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
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
}
