package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._
import scala.language.postfixOps

trait NewProject {
  self: AcceptanceSpec =>

  def createNewProject(implicit projectDetails: ProjectDetails, captureScreenshots: Boolean = false): Unit = {
    When("user clicks on the 'New Project' menu item")
    click on WelcomePage.TopBar.plusDropdown
    click on WelcomePage.TopBar.projectOption
    Then("the New Project page gets displayed")
    verify browserAt NewProjectPage

    When("user fills in and submits the new project details form")
    NewProjectPage submitFormWith projectDetails
    Then("the project gets created and the Project page gets displayed")
    val projectPage = ProjectPage()
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
    verify that projectPage.Files.Info.title is projectDetails.title.value
    And("the readme content")
    verify that projectPage.Files.Info.content contains "This is a Renku project"
  }
}
