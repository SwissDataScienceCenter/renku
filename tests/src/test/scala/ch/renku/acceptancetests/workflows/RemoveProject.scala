package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.GitLabPages.GitLabBaseUrl
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._
import scala.language.postfixOps

trait RemoveProject {
  self: AcceptanceSpec =>

  def removeProjectInGitLab(implicit projectDetails: ProjectDetails, gitLabBaseUrl: GitLabBaseUrl): Unit = {
    val projectPage = ProjectPage()
    When("the user clicks on the 'View in GitLab'")
    click on projectPage.viewInGitLab sleep (1 second)
    Then("a new tab with GitLab page should open")
    val gitLabPages = GitLabPages()
    verify browserSwitchedTo gitLabPages.ProjectPage
    When("the user navigates to the Project Settings")
    go to gitLabPages.SettingsPage
    verify browserSwitchedTo gitLabPages.SettingsPage
    And("they click on the Expand button in the Advanced section")
    click on gitLabPages.SettingsPage.Advanced.expandButton sleep (1 second)
    And("they click on the Remove project button")
    click on gitLabPages.SettingsPage.Advanced.removeProject sleep (1 second)
    And("they confirm the project removal")
    gitLabPages.SettingsPage.Advanced confirmRemoval projectDetails sleep (1 second)
    // Don't care where we go after removing the project
    // Then("they should get to the GitLab Projects page")
    // verify browserAt gitLabPages.projectsPage
  }

  def verifyProjectWasRemoved(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user switches back to the Renku tab")
    verify browserSwitchedTo projectPage
    And("they click on the Projects in the Top Bar")
    click on projectPage.TopBar.projects sleep (2 seconds)
    Then(s"the '${projectDetails.title}' project should not be listed")
    click on ProjectsPage.YourProjects.tab
    ProjectsPage.YourProjects.maybeLinkTo(projectDetails) shouldBe None
  }
}
