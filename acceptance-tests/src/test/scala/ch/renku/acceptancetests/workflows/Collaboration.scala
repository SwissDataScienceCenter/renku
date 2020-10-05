package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import ch.renku.acceptancetests.pages.GitLabPages.GitLabBaseUrl
import org.openqa.selenium.{WebDriver, WebElement}
import scala.jdk.CollectionConverters._
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}
import scala.language.postfixOps
import scala.concurrent.duration._

trait Collaboration {
  self: AcceptanceSpec =>

  def verifyMergeRequestsIsEmpty(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab
    And("they navigate to the Merge Requests sub tab")
    click on projectPage.Collaboration.MergeRequests.tab
    Then("they should see a 'No merge requests to display' info")
    verify userCanSee projectPage.Collaboration.MergeRequests.noMergeRequests
  }

  def verifyIssuesIsEmpty(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab
    And("they navigate to the Issues sub tab")
    click on projectPage.Collaboration.Issues.tab
    Then("they should see a 'No issues to display' info")
    verify userCanSee projectPage.Collaboration.Issues.noIssues
  }

  def createNewIssue(implicit projectDetails: ProjectDetails): Unit = {
    implicit val projectPage = ProjectPage()
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab
    And("they navigate to the Issues sub tab")
    click on projectPage.Collaboration.Issues.tab
    // Give some time for the tab change to take effect
    sleep(1 second)
    And("the user clicks on the 'New Issue' button")
    click on projectPage.Collaboration.Issues.newIssueLink

    And("they fill out the form")
    val issueTitle = "test issue"
    val issueDesc  = "test description"
    `create an issue with title and description`(issueTitle, issueDesc)

    Then("the new issue should be displayed in the list")
    val issueTitles = projectPage.Collaboration.Issues.issueTitles
    if (issueTitles.size < 1) fail("There should be at least one issue")
    issueTitles.find(_ == issueTitle) getOrElse fail("Issue with expected title could not be found.")
  }

  def `create an issue with title and description`(title: String, description: String)(implicit
      projectPage:                                        ProjectPage
  ): Unit = {
    val tf = projectPage.Collaboration.Issues.NewIssue.titleField
    tf.clear() sleep (1 second)
    tf enterValue title sleep (1 second)
    projectPage.Collaboration.Issues.NewIssue.markdownSwitch click;
    projectPage.Collaboration.Issues.NewIssue.descriptionField enterValue description sleep (1 second)
    projectPage.Collaboration.Issues.NewIssue.createIssueButton click;
    sleep(3 seconds)
  }

  def verifyBranchWasAdded(implicit projectDetails: ProjectDetails): Unit = {
    val projectPage = ProjectPage()
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab
    Then("they should see a 'Do you want to create a merge request for branch...' banner")
    verify userCanSee projectPage.Collaboration.MergeRequests.futureMergeRequestBanner
  }

  def createNewMergeRequest(implicit projectDetails: ProjectDetails): Unit = {
    implicit val projectPage = ProjectPage()
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab
    And("they navigate to the Merge Request sub tab")
    click on projectPage.Collaboration.MergeRequests.tab
    // Give some time for the tab change to take effect
    sleep(4 second)
    And("the user clicks on the 'Create Merge Request' button")
    click on projectPage.Collaboration.MergeRequests.createMergeRequestButton
    sleep(4 seconds)
    And("they navigate to the MergeRequest sub tab")
    click on projectPage.Collaboration.MergeRequests.tab
    sleep(3 seconds)
    Then("the new Merge Request should be displayed in the list")
    val mrTitles = projectPage.Collaboration.MergeRequests.mergeRequestsTitles
    if (mrTitles isEmpty) fail("There should be at least one merge request")
    mrTitles.find(_ == "test-branch") getOrElse fail("Merge Request with expected title could not be found.")
  }

  def createBranchInJupyterLab(jupyterLabPage: JupyterLabPage): Unit = {
    import jupyterLabPage.terminal
    And("Creates a test branch")
    terminal %> "git checkout -b test-branch" sleep (10 seconds)
    And("Adds some Python packages to the requirements.txt")
    terminal %> "echo pandas==0.25.3 >> requirements.txt" sleep (1 second)
    terminal %> "echo seaborn==0.9.0 >> requirements.txt" sleep (1 second)
    And("Pushes changes to branch")
    terminal %> "git add ." sleep (2 seconds)
    terminal %> "git push --set-upstream origin test-branch" sleep (3 seconds)
    And("Checks out master again")
    terminal %> "git checkout master" sleep (4 seconds)
  }

}
