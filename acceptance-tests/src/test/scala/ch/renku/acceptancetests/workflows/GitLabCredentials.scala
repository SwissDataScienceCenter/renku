/*
 * Copyright 2023 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.pages.{DashboardPage, GitLabUserProfilePage, GitLabWelcomePage}
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.concurrent.duration._

trait GitLabCredentials {
  self: Login with AcceptanceSpec =>

  def `verify user has GitLab credentials`: Unit =
    userCredentials.maybeGitLabAccessToken match {
      case Some(_) => ()
      case _ =>
        if (`check user has credentials`) ()
        else `add user credentials`
    }

  private def `add user credentials` = {

    `log in to Renku`

    verify browserSwitchedTo DashboardPage

    When("user clicks the GitLab button")
    DashboardPage.TopBar.`click on GitLab dropdown`
    And("the GitLab option")
    click on DashboardPage.TopBar.gitLabMenuItem
    sleep(1 second)

    Then("the user should be on the 'GitLab Welcome' page")
    verify browserSwitchedTo GitLabWelcomePage

    Then("the user goes to the 'Edit profile' page")
    GitLabWelcomePage.`click on the User Settings dropdown`
    click on GitLabWelcomePage.editProfileMenuOption sleep (2 seconds)

    verify browserAt GitLabUserProfilePage

    And("selects the 'Password' menu item")
    click on GitLabUserProfilePage.passwordsMenuItem

    And("enters password into the 'New password' field")
    GitLabUserProfilePage.Password.newPasswordInput enterValue userCredentials.password
    And("into the 'Password confirmation' field")
    GitLabUserProfilePage.Password.passwordConfirmationInput enterValue userCredentials.password
    And("saves")
    click on GitLabUserProfilePage.Password.saveButton
    sleep(2 seconds)

    Then("the user should have GitLab password set")
    if (!`check user has credentials`) fail("Setting password for the user in GitLab did not work")

    webDriver closeTab GitLabWelcomePage
  }
}
