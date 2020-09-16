package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

import org.scalatestplus.selenium.{Driver, WebBrowser}
import org.openqa.selenium.WebDriver

/**
  * Helper class for capturing screenshots for documentation.
  *
  * @param frame Object that can capture a screen
  * @param captureScreenshots True if should screenshot at checkpoint
  */
class DocsScreenshots(test: AcceptanceSpec, browser: WebBrowser with Driver) {

  lazy val captureScreenshots: Boolean = {
    Option(getProperty("docsrun")) orElse sys.env.get("RENKU_TEST_DOCS_RUN") match {
      case Some(s) => s.nonEmpty
      case None    => false
    }
  }

  /**
    * Informs helper to make a screenshot if we are capturing them.
    */
  def reachedCheckpoint(): Unit = {
    implicit val b:         WebBrowser with Driver = browser
    implicit val webDriver: WebDriver              = test.webDriver
    if (captureScreenshots)
      test.saveScreenshot
  }

}
