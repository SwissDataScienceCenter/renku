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
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

object GitLabUserProfilePage extends GitLabPage(path = "/-/profile", title = "Edit Profile · User Settings · GitLab") {

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = statusInput

  private def statusInput(implicit webDriver: WebDriver) =
    find(cssSelector(s"input#js-status-message-field"))

  def passwordsMenuItem(implicit webDriver: WebDriver): WebElement =
    find(cssSelector(s"a[data-qa-selector='profile_password_link']"))
      .getOrElse(fail("Menu 'Passwords' not found"))

  object Password {

    def newPasswordInput(implicit webDriver: WebDriver): WebElement =
      find(cssSelector(s"input#user_new_password"))
        .getOrElse(fail("'New password' input not found"))

    def passwordConfirmationInput(implicit webDriver: WebDriver): WebElement =
      find(cssSelector(s"input#user_password_confirmation"))
        .getOrElse(fail("'Password confirmation' input not found"))

    def saveButton(implicit webDriver: WebDriver): WebElement =
      find(cssSelector(s"input[type=submit]"))
        .getOrElse(fail("'Password confirmation' input not found"))
  }
}
