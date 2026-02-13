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

package ch.renku.acceptancetests.tooling

import ch.renku.acceptancetests.model.BaseUrl
import ch.renku.acceptancetests.pages.Page
import org.openqa.selenium.WebDriver
import org.scalatest.Assertions.fail

import scala.jdk.CollectionConverters._

trait WebDriverOps {

  implicit class WebDriverOps(webDriver: WebDriver) {

    def findTabWithUrlContaining(phrase: String): Option[WebDriver] =
      findTab(_.getCurrentUrl contains phrase)

    def switchToTab(idx: Int): WebDriver = {
      val allTabs = findAllTabs
      if (idx < allTabs.length) webDriver.switchTo() window findAllTabs(idx)
      else fail(s"There are only ${allTabs.length} open tab(s) in the browser; cannot open tab $idx")
    }

    def switchToTab[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): WebDriver =
      findTab(byUrlAndTitleOf(page))
        .getOrElse(
          fail(
            s"Cannot find window with URL starting with ${page.url} and title ${page.title}, " +
              s"instead I am at ${webDriver.getCurrentUrl} and title ${webDriver.getTitle}."
          )
        )

    def closeTab[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): Unit =
      findTab(byUrlAndTitleOf(page))
        .map(_.close())
        .getOrElse(
          fail(
            s"Cannot find window with URL starting with ${page.url} and title ${page.title}, " +
              s"instead I am at ${webDriver.getCurrentUrl} and title ${webDriver.getTitle}."
          )
        )

    private def findTab(predicate: WebDriver => Boolean) = findAllTabs
      .foldLeft(Option.empty[WebDriver]) {
        case (someDriver @ Some(_), _) => someDriver
        case (None, tabName) =>
          val driver = webDriver.switchTo() window tabName
          Some(driver).filter(predicate)
      }

    private def byUrlAndTitleOf[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): WebDriver => Boolean =
      driver => (driver.getCurrentUrl startsWith page.url) && page.titleRegex.matches(driver.getTitle)

    private def findAllTabs = webDriver.getWindowHandles.asScala.toArray
  }
}
