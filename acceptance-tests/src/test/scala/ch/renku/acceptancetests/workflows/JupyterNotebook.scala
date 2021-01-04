package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.JupyterLabPage
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}

import scala.concurrent.duration._
import scala.language.postfixOps

trait JupyterNotebook extends Datasets {
  self: AcceptanceSpec =>

  def `create a dataset`(jupyterLabPage: JupyterLabPage, datasetName: DatasetName): Unit = {
    import jupyterLabPage.terminal

    terminal %> s"renku dataset create '$datasetName'" sleep (2 seconds)
    And("pushes it")
    terminal %> "git push" sleep (30 seconds)
  }

  def verifyUserCanWorkWithJupyterNotebook(implicit
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

    Then("the user can see the created dataset")
    verifyDatasetCreated(datasetName)
  }

}
