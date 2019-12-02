package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.pages.Page.{Path, Title}
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser._

case object LandingPage extends RenkuPage {

  override val path:  Path  = "/"
  override val title: Title = "Renku"

  def loginButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(".btn.btn-primary.btn-lg")) getOrElse fail("Login button not found")
  }
}
