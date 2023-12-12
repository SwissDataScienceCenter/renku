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

package ch.renku.acceptancetests.tooling

import TestLogger._
import cats.syntax.all._
import ch.renku.acceptancetests.model.BaseUrl
import ch.renku.acceptancetests.pages.Page
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.exceptions.TestFailedException
import org.scalatest.matchers.should
import org.scalatestplus.selenium
import org.scalatestplus.selenium.WebBrowser

import scala.concurrent.duration._
import scala.language.implicitConversions

trait Grammar extends WebElementOps with WebDriverOps with Eventually {
  self: WebBrowser with WebDriveredSpec with should.Matchers =>

  implicit def toSeleniumPage[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): selenium.Page =
    new selenium.Page {
      override val url = page.url
    }

  object sleep {
    def apply(duration: Duration): Unit = Page SleepThread duration
  }

  def logAndFail(message: String): Nothing = {
    logger.error(message)
    fail(message)
  }

  object verify {

    def that(element: => WebElement): WebElement = element

    def browserAt[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): Unit = eventually {
      currentUrl.toLowerCase               should startWith(page.url.toLowerCase)
      page.titleRegex.matches(pageTitle) shouldBe true
      verifyElementsAreDisplayed(page)
    }

    private def verifyElementsAreDisplayed[Url <: BaseUrl](page: Page[Url]): Unit =
      page.pageReadyElement.map(_.isDisplayed) match {
        case Some(false) => fail(s"'${page.title}' page elements are not displayed")
        case _           => ()
      }

    def browserSwitchedTo[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): Unit = eventually {
      webDriver.switchToTab(page)
    }

    def userCanSee(element: => WebElement): Unit = eventually {
      element.isDisplayed shouldBe true
    }
  }

  object reload {

    @scala.annotation.tailrec
    def whenUserCannotSee(findElement: WebDriver => WebElement, attempt: Int = 1): Unit =
      if (
        attempt <= 20 &&
        Either
          .catchOnly[TestFailedException](!findElement(webDriver).isDisplayed)
          .fold(_ => true, identity)
      ) {
        sleep(3 seconds)
        webDriver.navigate().refresh()
        sleep(2 seconds)
        whenUserCannotSee(findElement, attempt + 1)
      } else
        findElement(webDriver).isDisplayed
  }

  @scala.annotation.tailrec
  final def `try few times with page reload`[V](section: WebDriver => V, attempt: Int = 1)(implicit
      webDriver: WebDriver
  ): V =
    Either.catchOnly[RuntimeException](section(webDriver)) match {
      case Right(successValue) => successValue
      case Left(error) if attempt > 20 =>
        logger.error("Number of attempts exceeded", error)
        throw error
      case Left(_) =>
        sleep(3 seconds)
        reloadPage()
        sleep(2 seconds)
        `try few times with page reload`(section, attempt + 1)
    }

  @scala.annotation.tailrec
  final def `try few times without page reload`[V](section: WebDriver => V, attempt: Int = 1)(implicit
      webDriver: WebDriver
  ): V =
    Either.catchOnly[RuntimeException](section(webDriver)) match {
      case Right(successValue)         => successValue
      case Left(error) if attempt > 10 => throw error
      case Left(_) =>
        sleep(2 seconds)
        `try few times without page reload`(section, attempt + 1)
    }

  object pause {

    @scala.annotation.tailrec
    def asLongAsBrowserAt[Url <: BaseUrl](page: Page[Url], attempt: Int = 1)(implicit baseUrl: Url): Unit = {
      val maxAttempts   = 120
      val checkInterval = 1 second

      if (attempt <= maxAttempts && (currentUrl startsWith page.url)) {
        sleep(checkInterval)
        asLongAsBrowserAt(page, attempt + 1)
      } else if (attempt > maxAttempts && (currentUrl startsWith page.url))
        fail {
          s"Expected to be redirected from ${page.url} page but got stuck on it for ${(checkInterval * attempt).toSeconds}s"
        }
    }

    @scala.annotation.tailrec
    def whenUserCanSee(findElement: WebDriver => Option[WebElement], attempt: Int = 1): Unit =
      if (attempt <= 30 && findElement(webDriver).fold(ifEmpty = false)(_.isDisplayed)) {
        sleep(1 second)
        webDriver.navigate().refresh()
        sleep(2 seconds)
        whenUserCanSee(findElement, attempt + 1)
      } else ()
  }

  def unless(test: Boolean)(testFun: => Any): Unit =
    if (!test) testFun

  protected implicit class WebElementGrammar(element: WebElement) {
    def is(expected:       String): Unit = element.getText               shouldBe expected
    def contains(expected: String): Unit = element.getText                 should include(expected)
    def matches(pattern:   String): Unit = element.getText                 should fullyMatch regex pattern
    def hasValue(expected: String): Unit = element.getAttribute("value") shouldBe expected
    def attributeContains(attribute: String, expected: String): Unit =
      element.getAttribute(attribute) should include(expected)
  }

  protected implicit class OperationOps(unit: Unit) {
    def sleep(duration: Duration): Unit = Page SleepThread duration
  }

  protected implicit def toWebElement(element: WebBrowser.Element): WebElement =
    element.underlying
}
