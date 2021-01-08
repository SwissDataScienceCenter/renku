package ch.renku.acceptancetests.tooling

import ch.renku.acceptancetests.workflows.{Environments, JupyterNotebook}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeDriverService, ChromeOptions}
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers => ScalatestMatchers}
import org.scalatestplus.selenium.{Chrome, Driver, WebBrowser}
import scala.language.postfixOps

trait AcceptanceSpec
    extends FeatureSpec
    with GivenWhenThen
    with BeforeAndAfterAll
    with Matchers
    with ScalatestMatchers
    with WebBrowser
    with Environments
    with Driver
    with Grammar
    with JupyterNotebook
    with ScreenCapturingSpec
    with AcceptanceSpecData
    with AcceptanceSpecPatience {

  protected implicit val browser: AcceptanceSpec = this
  implicit lazy val webDriver:    WebDriver      = getWebDriver

  private def getWebDriver: WebDriver =
    sys.env.get("DOCKER") match {
      case Some(_) =>
        new ChromeDriver(
          new ChromeDriverService.Builder().withWhitelistedIps("127.0.0.1").build,
          new ChromeOptions().addArguments("--no-sandbox", "--headless", "--disable-gpu")
        )
      case None =>
        Chrome.webDriver
    }

}
