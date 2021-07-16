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

import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._

trait Collaboration {
  self: AcceptanceSpec with Project =>

  def `navigate to the merge requests tab`: Unit = {
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab sleep (1 second)
    And("they navigate to the Merge Requests sub tab")
    click on projectPage.Collaboration.MergeRequests.tab sleep (1 second)
  }

  def `verify there are no merge requests`: Unit = {
    Then("they should see a 'No merge requests to display' info")
    verify userCanSee projectPage.Collaboration.MergeRequests.noMergeRequests
  }

  def `navigate to the issues tab`: Unit = {
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab sleep (1 second)
    And("they navigate to the Issues sub tab")
    click on projectPage.Collaboration.Issues.tab sleep (1 second)
  }

  def `verify there are no issues`: Unit = {
    Then("they should see a 'No issues to display' info")
    verify userCanSee projectPage.Collaboration.Issues.noIssues
  }

  def `create a new issue`(title: String, description: String): Unit = {
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab sleep (1 second)
    And("they navigate to the Issues sub tab")
    click on projectPage.Collaboration.Issues.tab sleep (2 seconds)
    And("the user clicks on the 'New Issue' button")
    click on projectPage.Collaboration.Issues.newIssueLink sleep (1 second)

    And("they fill out the form")
    `create an issue with title and description`(title, description)
  }

  def `view the issue`(issueTitle: String): Unit =
    `try few times before giving up` { _ =>
      Then("the new issue should be displayed in the list")
      val issueTitles = projectPage.Collaboration.Issues.issueTitles
      if (issueTitles.size < 1) fail("There should be at least one issue")
      issueTitles.find(_ == issueTitle) getOrElse fail("Issue with expected title could not be found.")
    }

  def `create an issue with title and description`(title: String, description: String): Unit = {
    val tf = projectPage.Collaboration.Issues.NewIssue.titleField
    tf.clear() sleep (1 second)
    tf enterValue title
    click on projectPage.Collaboration.Issues.NewIssue.markdownSwitch sleep (1 second)
    projectPage.Collaboration.Issues.NewIssue.descriptionField enterValue description
    click on projectPage.Collaboration.Issues.NewIssue.createIssueButton sleep (1 second)
  }

  def `verify branch was added`: Unit = {
    When("the user navigates to the Collaboration tab")
    click on projectPage.Collaboration.tab sleep (1 second)
    Then("they should see a 'Do you want to create a merge request for branch...' banner")
    verify userCanSee projectPage.Collaboration.MergeRequests.futureMergeRequestBanner
  }

  def `create a new merge request`: Unit = {
    `try few times before giving up` { _ =>
      When("the user navigates to the Collaboration tab")
      click on projectPage.Collaboration.tab sleep (1 second)
      And("they navigate to the Merge Request sub tab")
      click on projectPage.Collaboration.MergeRequests.tab sleep (5 second)
      And("they can see the 'Create Merge Request' button")
      verify userCanSee projectPage.Collaboration.MergeRequests.createMergeRequestButton
    }
    And("the user clicks on the 'Create Merge Request' button")
    click on projectPage.Collaboration.MergeRequests.createMergeRequestButton sleep (5 second)
    And("they navigate to the MergeRequest sub tab")
    click on projectPage.Collaboration.MergeRequests.tab sleep (4 second)
  }

  def `view the merge request`(title: String): Unit = {
    Then("the new Merge Request should be displayed in the list")
    val mrTitles = projectPage.Collaboration.MergeRequests.mergeRequestsTitles
    if (mrTitles.isEmpty) fail("There should be at least one merge request")
    mrTitles.find(_ == title) getOrElse fail("Merge Request with expected title could not be found.")
  }

  def `create a branch in JupyterLab`(jupyterLabPage: JupyterLabPage, branchName: String): Unit = {
    import jupyterLabPage.terminal
    And("Creates a test branch")
    terminal %> "git checkout -b test-branch" sleep (10 seconds)
    And("Adds some Python packages to the requirements.txt")
    terminal %> "echo pandas==1.3.0 >> requirements.txt" sleep (1 second)
    terminal %> "echo seaborn==0.11.1 >> requirements.txt" sleep (1 second)
    And("Pushes changes to branch")
    terminal %> "git add ." sleep (2 seconds)
    terminal %> "git push --set-upstream origin test-branch" sleep (3 seconds)
    And("Checks out master again")
    terminal %> "git checkout master" sleep (4 seconds)
  }
}
