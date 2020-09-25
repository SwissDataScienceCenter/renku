package ch.renku.acceptancetests.tooling

import cats.implicits._
import ch.renku.acceptancetests.pages.Page
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.exceptions.TestFailedException
import org.scalatestplus.selenium
import org.scalatestplus.selenium.WebBrowser

import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.language.{implicitConversions, postfixOps}

trait Grammar extends Eventually {
  self: WebBrowser with AcceptanceSpec =>

  implicit def toSeleniumPage[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): selenium.Page =
    new selenium.Page {
      override val url = page.url
    }

  object sleep {
    def apply(duration: Duration): Unit = Page SleepThread duration
  }

  object verify {

    def that(element: => WebElement): WebElement = element

    def browserAt[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): Unit = eventually {
      currentUrl should startWith(page.url)
      pageTitle  shouldBe page.title.toString()
      verifyElementsAreDisplayed(page)
    }

    private def verifyElementsAreDisplayed[Url <: BaseUrl](page: Page[Url]): Unit =
      page.pageReadyElement.map(_.isDisplayed) match {
        case Some(false) => fail(s"'${page.title}' page elements are not displayed")
        case _           => ()
      }

    def browserSwitchedTo[Url <: BaseUrl](page: Page[Url])(implicit baseUrl: Url): Unit = eventually {
      if (webDriver.getWindowHandles.asScala exists forTabWith(page)) ()
      else throw new Exception(s"Cannot find window with ${page.url} and title ${page.title.toString()}")
    }

    private def forTabWith[Url <: BaseUrl](page: Page[Url])(handle: String)(implicit baseUrl: Url): Boolean = {
      webDriver.switchTo() window handle
      (currentUrl startsWith page.url) && (pageTitle == page.title.toString())
    }

    def userCanSee(element: => WebElement): Unit = eventually {
      element.isDisplayed shouldBe true
    }
  }

  object reload {

    @scala.annotation.tailrec
    def whenUserCannotSee(element: WebDriver => WebElement, attempt: Int = 1): Unit =
      if (attempt <= 10 && Either
            .catchOnly[TestFailedException](!element(webDriver).isDisplayed)
            .fold(_ => true, identity)) {
        sleep(5 seconds)
        webDriver.navigate().refresh()
        whenUserCannotSee(element, attempt + 1)
      } else
        element(webDriver).isDisplayed
  }

  object pause {

    @scala.annotation.tailrec
    def asLongAsBrowserAt[Url <: BaseUrl](page: Page[Url], attempt: Int = 1)(implicit baseUrl: Url): Unit = {
      val frequencyFactor = 3
      val maxAttempts     = 10 * frequencyFactor

      if (attempt <= maxAttempts && (currentUrl startsWith page.url)) {
        sleep((patienceConfig.timeout.millisPart / frequencyFactor) millis)
        asLongAsBrowserAt(page, attempt + 1)
      } else if (attempt > maxAttempts && (currentUrl startsWith page.url))
        fail {
          s"Expected to be redirected from the ${page.path} but " +
            s"it did not happen after ${((patienceConfig.timeout.millisPart millis) * attempt).toSeconds}s"
        }
    }
  }

  def unless(test: Boolean)(testFun: => Any): Unit =
    if (!test) testFun

  protected implicit class WebElementGrammar(element: WebElement) {
    def is(expected:       String): Unit = element.getText               shouldBe expected
    def contains(expected: String): Unit = element.getText               should include(expected)
    def matches(pattern:   String): Unit = element.getText               should fullyMatch regex pattern
    def hasValue(expected: String): Unit = element.getAttribute("value") shouldBe expected
  }

  protected implicit class OperationOps(unit: Unit) {
    def sleep(duration: Duration): Unit = Page SleepThread duration
  }

  protected implicit def toWebElement(element: WebBrowser.Element): WebElement =
    element.underlying

  protected implicit class ElementOps(element: WebBrowser.Element) {

    def enterValue(value: String): Unit = value foreach { char =>
      element.sendKeys(char.toString) sleep (100 millis)
    }
  }

  protected implicit class WebElementOps(element: WebElement) {

    def enterValue(value: String): Unit = value foreach { char =>
      element.sendKeys(char.toString) sleep (100 millis)
    }
  }
}
