package ch.renku.acceptancetests.tooling

import ch.renku.acceptancetests.workflows.{Environments, JupyterNotebook}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
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
      // NOTE We could also support running against the selenium container from the host machine:
      // RemoteWebDriver.builder().url("http://localhost:4444/wd/hub").addAlternative(new ChromeOptions()).build()
      case Some(_) =>
        RemoteWebDriver.builder().url("http://chrome:4444/wd/hub").addAlternative(new ChromeOptions()).build()
      case None =>
        Chrome.webDriver
    }

}
