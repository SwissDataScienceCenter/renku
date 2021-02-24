/*
 * Copyright 2021 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.datasets
import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.{DatasetPage, ProjectPage}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}

import scala.concurrent.duration._

trait Datasets {
  self: AcceptanceSpec with KnowledgeGraphApi =>

  def verifyDatasetCreated(datasetName: DatasetName)(implicit projectDetails: ProjectDetails): Unit = {
    implicit val projectPage: ProjectPage = ProjectPage()
    val datasetPage = DatasetPage(datasetName)
    When("the user navigates to the Datasets tab")
    click on projectPage.Datasets.tab

    And("the events are processed")
    val datasetPageTitle = projectPage.Datasets.title(_: WebDriver)
    reload whenUserCannotSee datasetPageTitle

    Then(s"the user should see a link to the '$datasetName' dataset")
    val datasetLink = projectPage.Datasets.DatasetsList.link(to = datasetPage)(_: WebDriver)
    reload whenUserCannotSee datasetLink
  }

  def `create a dataset`(datasetName: DatasetName)(implicit projectPage: ProjectPage): DatasetPage = {
    import Modification._
    val newDatasetName = datasets.DatasetName("new")
    val newDatasetPage = DatasetPage(newDatasetName)
    Given("the user is on the Datasets tab")
    click on projectPage.Datasets.tab

    When("the user clicks on the Add Dataset button")
    click on projectPage.Datasets.addADatasetButton
    verify that newDatasetPage.ModificationForm.formTitle contains "Add Dataset"

    And(s"the user add the title '$datasetName' to the new dataset")
    `changing its title`(to = datasetName.toString).modifying(newDatasetPage)

    And("the user saves the changes")
    click on newDatasetPage.ModificationForm.datasetSubmitButton
    pause asLongAsBrowserAt newDatasetPage

    And("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectPage.asProjectIdentifier)

    val datasetPage = DatasetPage(datasetName)
    Then("the user should see its newly created dataset and the project it belongs to")
    reloadPage() sleep (1 second)
    verify browserAt datasetPage
    verify that datasetPage.datasetTitle contains datasetName.value
    datasetPage.ProjectsList.link(projectPage).isDisplayed shouldBe true

    datasetPage
  }

  def `navigate to dataset`(datasetPage: DatasetPage)(implicit projectPage: ProjectPage): Unit = {

    Given("the user is on the Datasets tab")
    click on projectPage.Datasets.tab

    When(s"the user clicks on the dataset name")
    click on projectPage.Datasets.DatasetsList.link(to = datasetPage)

    Then(s"the user should see the dataset details")
    verify browserAt datasetPage
  }

  def `modify the dataset`(datasetPage: DatasetPage, by: Modification, and: Modification*)(implicit
      projectPage:                      ProjectPage
  ): DatasetPage = {
    Given(s"the user is on the page of the dataset")
    `navigate to dataset`(datasetPage)

    When("the user clicks on the modify button")
    click on datasetPage.modifyButton
    verify userCanSee datasetPage.ModificationForm.formTitle

    And(s"the user modifies the dataset by ${by.name}")
    by.modifying(datasetPage)
    and.toList.foreach { by =>
      And(s"by ${by.name}")
      by.modifying(datasetPage)
    }

    And("the user saves the modification")
    click on datasetPage.ModificationForm.datasetSubmitButton

    verify browserAt datasetPage

    And("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectPage.asProjectIdentifier)

    Then("the user should see its dataset and to which project it belongs")
    reloadPage() sleep (1 second)
    datasetPage.ProjectsList.link(projectPage).isDisplayed shouldBe true

    datasetPage
  }

  case class Modification private (name: String, field: DatasetPage => WebElement, newValue: String) {
    def modifying(datasetPage: DatasetPage): Unit = field(datasetPage).enterValue(newValue)
  }

  object Modification {

    def `changing its title`(to: String): Modification =
      Modification("changing its title", datasetPage => datasetPage.ModificationForm.datasetTitleField, to)

    def `changing its description`(to: String): Modification =
      Modification("changing its description", datasetPage => datasetPage.ModificationForm.datasetDescriptionField, to)
  }
}
