package ch.renku.acceptancetests

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators.paragraph
import ch.renku.acceptancetests.model.datasets.{DatasetName, DatasetTitle}
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.ProjectPage
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots}
import ch.renku.acceptancetests.workflows._

import scala.language.postfixOps

class DatasetModificationFlow extends AcceptanceSpec with Login with NewProject with RemoveProject with Datasets {

  scenario("From the UI the user can modify a dataset and only interact with its latest version") {
    import Modification._
    implicit val loginType:       LoginType       = logIntoRenku
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)
    implicit val projectDetails:  ProjectDetails  = ProjectDetails.generate()

    implicit val projectPage = ProjectPage()
    val datasetName          = DatasetName.generate
    val newDescription       = paragraph().generateOne
    createNewProject(projectDetails)

    Given("a new dataset")
    val originalDatasetPage = `create a dataset`(datasetName)

    val newTitle = DatasetTitle.generate
    When("the user modifies the dataset")
    `modify the dataset`(originalDatasetPage,
                         by = `changing its title`(to = newTitle.toString),
                         and = `changing its description`(to = newDescription.value)
    )

    val secondTitle = DatasetTitle.generate
    And("the user modifies a 2nd time the dataset")
    `modify the dataset`(originalDatasetPage, by = `changing its title`(to = secondTitle.toString))

    Then("the user should see only the latest version of the dataset")
    verifyDatasetCreated(datasetName)

    removeProjectInGitLab
    verifyProjectWasRemovedInRenku

    logOutOfRenku
  }
}
