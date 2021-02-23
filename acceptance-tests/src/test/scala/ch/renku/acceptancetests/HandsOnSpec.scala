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
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows._
import org.openqa.selenium.JavascriptExecutor

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Run the HandsOn from the documentation.
  */
class HandsOnSpec
    extends AcceptanceSpec
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with JupyterNotebook
    with FlightsTutorial
    with Datasets
    with KnowledgeGraphApi {

  scenario("User can do hands-on tutorial") {

    implicit val loginType: LoginType = `log in to Renku`

    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)

    implicit val projectDetails: ProjectDetails =
      ProjectDetails.generateHandsOnProject(docsScreenshots.captureScreenshots)

    createNewProject(projectDetails)

    val projectHttpUrl     = findProjectHttpUrl
    val flightsDatasetName = followTheFlightsTutorialOnUsersMachine(projectHttpUrl)

    When("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectDetails.asProjectIdentifier)

    verifyDatasetCreated(flightsDatasetName)

    verifyUserCanWorkWithJupyterNotebook

    verifyAnalysisRan

    verifyMergeRequestsIsEmpty
    verifyIssuesIsEmpty
    createNewIssue

    setProjectTags
    setProjectDescription
    `remove project in GitLab`(projectDetails)
    `verify project is removed`

    `log out of Renku`
  }
  def verifyAnalysisRan(implicit projectDetails: ProjectDetails, docsScreenshots: DocsScreenshots): Unit = {
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
    // Scroll to the bottom of the page
    webDriver.asInstanceOf[JavascriptExecutor].executeScript("window.scrollBy(0,document.body.scrollHeight)")
    docsScreenshots.reachedCheckpoint()

    val resultCell = projectPage.Files.Notebook.cellWithText("There were 4951 flights to Austin, TX in Jan 2019.")
    verify that resultCell contains "There were 4951 flights to Austin, TX in Jan 2019."
  }
}
