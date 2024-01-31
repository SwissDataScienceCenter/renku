/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
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

package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.GitLabBaseUrl
import ch.renku.acceptancetests.model.users.UserCredentials
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

object GitLabWelcomePage extends GitLabPage(path = "", title = "Projects · Dashboard · GitLab") {

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = menuDropDown

  def menuDropDown(implicit webDriver: WebDriver) = find(cssSelector(s"a[href='#']"))

  def `click on the User Settings dropdown`(implicit
      userCredentials: UserCredentials,
      gitLabBaseUrl:   GitLabBaseUrl,
      webDriver:       WebDriver
  ): Unit = new Actions(webDriver)
    .moveToElement(userSettingsDropDown)
    .click()
    .build()
    .perform();

  private def userSettingsDropDown(implicit
      userCredentials: UserCredentials,
      gitLabBaseUrl:   GitLabBaseUrl,
      webDriver:       WebDriver
  ) = find(cssSelector(s"a[href='$gitLabBaseUrl/${userCredentials.username}']"))
    .getOrElse(fail("Top right 'User Settings' button not found"))

  def editProfileMenuOption(implicit gitLabBaseUrl: GitLabBaseUrl, webDriver: WebDriver) =
    find(cssSelector(s"a[href='$gitLabBaseUrl/-/profile']"))
      .getOrElse(fail("'Edit profile' menu option not found"))
}
