package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.pages.JupyterLabPage
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._
import scala.language.postfixOps

trait JupyterNotebook {
  self: AcceptanceSpec =>

  def `create a dataset`(jupyterLabPage: JupyterLabPage, datasetName: DatasetName): Unit = {
    import jupyterLabPage.terminal

    terminal %> s"renku dataset create '$datasetName'" sleep (2 seconds)
    And("pushes it")
    terminal %> "git push" sleep (30 seconds)
  }
}
