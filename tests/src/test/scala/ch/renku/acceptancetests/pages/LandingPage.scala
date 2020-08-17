package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.pages.Page.{Path, Title}
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser._

case object LandingPage extends RenkuPage {

  override val path:  Path  = "/"
  override val title: Title = "Renku"

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(loginButton)

  def loginButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#login-button")) getOrElse fail("Login button not found")
  }
}
