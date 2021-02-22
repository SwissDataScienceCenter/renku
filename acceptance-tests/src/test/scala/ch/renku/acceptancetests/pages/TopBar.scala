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

package ch.renku.acceptancetests.pages

import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

trait TopBar {
  self: RenkuPage =>

  object TopBar {

    def projects(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("ul.navbar-nav.mr-auto a[href='/projects']")) getOrElse fail(
        "Top Right 'Projects' link not found"
      )
    }

    def plusDropdown(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("#plus-dropdown")) getOrElse fail("Top Right '+' not found")
    }

    def projectOption(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("#navbar-project-new")) getOrElse fail("Project option not found")
    }

    def topRightDropDown(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("#profile-dropdown"))
        .getOrElse(fail("Top Right dropdown not found"))
    }

    def logoutLink(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("#logout-link")) getOrElse fail("Logout link not found")
    }
  }
}
