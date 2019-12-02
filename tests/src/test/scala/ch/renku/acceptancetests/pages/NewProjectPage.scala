package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import ch.renku.acceptancetests.tooling.ScreenCapturing
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.{Driver, WebBrowser}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

import scala.concurrent.duration._
import scala.language.postfixOps

case object NewProjectPage extends RenkuPage with TopBar with ScreenCapturing {

  override val path:  Path  = "/project_new"
  override val title: Title = "Renku"

  def submitFormWith(
      project:          ProjectDetails
  )(implicit webDriver: WebDriver, browser: WebBrowser with Driver, captureScreenshots: Boolean = false): Unit =
    eventually {
      titleField.clear() sleep (1 second)
      titleField.sendKeys(project.title.value) sleep (1 second)

      descriptionField.clear() sleep (1 second)
      descriptionField.sendKeys(project.description.value) sleep (1 second)

      if (captureScreenshots) writeScreenshot

      createButton.click() sleep (1 second)
    }

  private def titleField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#title")) getOrElse fail("Title field not found")
  }

  private def descriptionField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("textarea#description")) getOrElse fail("Description field not found")
  }

  def createButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("form button.btn.btn-primary:last-of-type")) getOrElse fail("Create button not found")
  }
}
