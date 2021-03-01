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

package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows._

import scala.concurrent.duration._

/**
  * Run the HandsOn from the documentation.
  */
class HandsOnSpec
    extends AcceptanceSpec
    with Environments
    with Login
    with Project
    with Settings
    with JupyterNotebook
    with FlightsTutorial
    with Datasets
    with KnowledgeGraphApi {

  Scenario("User can do hands-on tutorial") {

    `log in to Renku`

    `create, open or continue with a project`

    val projectUrl         = `find project Http URL in the Settings Page`
    val flightsDatasetName = `follow the flights tutorial`(projectUrl)

    When("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectDetails.asProjectIdentifier)

    `verify dataset was created`(flightsDatasetName)

    `verify user can work with Jupyter notebook`

    `verify analysis was ran`

    `log out of Renku`
  }

  private def `verify analysis was ran`(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Files tab")
    click on projectPage.Files.tab
    And("they click on the notebooks folder in the File View")
    click on (projectPage.Files.FileView folder "notebooks") sleep (2 seconds)
    And("they click on the 01-CountFlights.ran.ipynb file in the File View")
    click on (projectPage.Files.FileView file "notebooks/01-CountFlights.ran.ipynb") sleep (2 seconds)
    Then("they should see a file header with the notebook filename")
    verify that projectPage.Files.Info.heading contains "notebooks/01-CountFlights.ran.ipynb"
    And("the correct notebook content")
    docsScreenshots.takeScreenshot(executeBefore = "window.scrollBy(0,document.body.scrollHeight)")
    val resultCell = projectPage.Files.Notebook.cellWithText("There were 4951 flights to Austin, TX in Jan 2019.")
    verify that resultCell contains "There were 4951 flights to Austin, TX in Jan 2019."
  }
}
