package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.pages.Page._
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

case object WelcomePage extends RenkuPage with TopBar {
  override val path:  Path  = "/"
  override val title: Title = "Renku"

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(yourProjectsSection)

  private def yourProjectsSection(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(".order-2.col-md-6.order-md-1")) getOrElse fail("Your Projects section not found")
  }
}
