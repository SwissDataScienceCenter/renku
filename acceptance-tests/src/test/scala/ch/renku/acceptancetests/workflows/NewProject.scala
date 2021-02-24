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

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._

trait NewProject {
  self: AcceptanceSpec =>

  def createNewProject(projectDetails: ProjectDetails): Unit = {
    When("user clicks on the 'New Project' menu item")
    click on WelcomePage.TopBar.plusDropdown
    click on WelcomePage.TopBar.projectOption
    Then("the New Project page gets displayed")
    verify browserAt NewProjectPage

    When("user fills in and submits the new project details form")
    NewProjectPage submitFormWith projectDetails
    pause asLongAsBrowserAt NewProjectPage
    Then(s"the project '${projectDetails.title}' gets created and the Project page gets displayed")

    val projectPage = ProjectPage createFrom projectDetails
    verify browserAt projectPage

    When("the user navigates to the Overview -> Description tab")
    click on projectPage.Overview.tab
    click on projectPage.Overview.descriptionButton
    Then("they should see project's README.md content")
    verify that projectPage.Overview.Description.title is "README.md"

    When("the user navigates to the Files tab")
    click on projectPage.Files.tab
    And("they click on the README.md file in the File View")
    click on (projectPage.Files.FileView file "README.md") sleep (2 seconds)
    Then("they should see the file header")
    verify that projectPage.Files.Info.heading contains "README.md"
    And("the commit hash")
    verify that projectPage.Files.Info.commit matches "^Commit: [0-9a-f]{8}$"
    And("creator name and timestamp")
    verify that projectPage.Files.Info.creatorAndTime contains userCredentials.fullName.value
    And("the readme header which is the project title")
    verify that projectPage.Files.Info.title is projectDetails.readmeTitle
    And("the readme content")
    verify that projectPage.Files.Info.content contains "This is a Renku project"
  }
}
