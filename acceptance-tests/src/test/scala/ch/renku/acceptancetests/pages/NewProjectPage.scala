package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Visibility}
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import ch.renku.acceptancetests.tooling.ScreenCapturing
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}
import org.scalatestplus.selenium.{Driver, WebBrowser}

import scala.concurrent.duration._
import scala.language.postfixOps

case object NewProjectPage extends RenkuPage with TopBar with ScreenCapturing {

  override val path:  Path  = "/projects/new"
  override val title: Title = "Renku"

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(createButton)

  def submitFormWith(
      project:          ProjectDetails
  )(implicit webDriver: WebDriver, browser: WebBrowser with Driver, captureScreenshots: Boolean = false): Unit =
    eventually {
      titleField.clear() sleep (5 seconds)
      titleField.enterValue(project.title.value) sleep (1 second)

      visibilityField.click() sleep (1 second)
      visibilityOption(project.visibility).click() sleep (1 second)

      templateField.click() sleep (1 second)
      templateOption.click() sleep (1 second)
      templateField.click() sleep (1 second)

      descriptionField.clear() sleep (1 second)
      descriptionField.enterValue(project.description.value) sleep (1 second)

      if (captureScreenshots) saveScreenshot

      createButton.click() sleep (5 seconds)
    }

  private def titleField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#title")) getOrElse fail("Title field not found")
  }

  private def visibilityField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("select#visibility")) getOrElse fail("Visibility field not found")
  }

  private def visibilityOption(visibility: Visibility)(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(s"option[value='${visibility.value}']")) getOrElse fail("Visibility option not found")
  }

  private def templateField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("select#template")) getOrElse fail("Template field not found ")
  }

  private def templateOption(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("option[value='Renku/python-minimal']")) getOrElse fail("Template option not found")
  }

  private def descriptionField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#parameter-description")) getOrElse fail("Description parameter field not found")
  }

  def createButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#create-new-project")) getOrElse fail("Create button not found")
  }
}
