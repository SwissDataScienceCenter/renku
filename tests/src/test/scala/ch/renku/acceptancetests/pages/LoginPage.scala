package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.Page._
import ch.renku.acceptancetests.tooling._
import ch.renku.acceptancetests.workflows.LoginType
import ch.renku.acceptancetests.workflows.LoginType.{LoginWithProvider, LoginWithoutProvider}
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers => ScalatestMatchers}
import org.scalatestplus.selenium
import org.scalatestplus.selenium.WebBrowser._

import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

case object LoginPage extends RenkuPage {

  override val path:  Path  = "/auth/realms/Renku/protocol/openid-connect/auth"
  override val title: Title = "Log in to Renku"

  /**
    * When a renku instance uses another openID connect provider, the user may get a second login page.
    */
  private case class ProviderLoginPage(userCredentials: UserCredentials)
      extends selenium.Page
      with ScalatestMatchers
      with Eventually
      with AcceptanceSpecPatience {

    final override lazy val url: String = "https://dev.renku.ch/auth/realms/Renku/protocol/openid-connect/auth"

    def logIn(implicit webDriver: WebDriver): Unit = eventually {
      usernameField.clear() sleep (1 second)
      usernameField.sendKeys(userCredentials.email.value) sleep (1 second)

      passwordField.clear() sleep (1 second)
      passwordField.sendKeys(userCredentials.password.value) sleep (1 second)

      logInButton.click()
    }

    def usernameField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#username")) getOrElse fail("Username field not found")
    }

    def passwordField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#password")) getOrElse fail("Password field not found")
    }

    def logInButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(".btn.btn-primary.btn-block.btn-lg")) getOrElse fail("Log In button not found")
    }
  }

  def logInWith(userCredentials: UserCredentials)(implicit webDriver: WebDriver): LoginType = eventually {
    if (userCredentials.useProvider) {
      oidcButton.click() sleep (5 seconds)
      val providerLoginPage = ProviderLoginPage(userCredentials)
      if (!(currentUrl startsWith providerLoginPage.url)) {
        println("Not on OpenID login provider page, trying again")
        oidcButton.click() sleep (5 seconds)
      }
      providerLoginPage.logIn sleep (5 seconds)
      LoginWithProvider
    } else {
      usernameField.clear() sleep (1 second)
      usernameField.sendKeys(userCredentials.email.value) sleep (1 second)

      passwordField.clear() sleep (1 second)
      passwordField.sendKeys(userCredentials.password.value) sleep (1 second)

      logInButton.click()

      val providerLoginPage = ProviderLoginPage(userCredentials)
      if (currentUrl startsWith providerLoginPage.url) {
        providerLoginPage.logIn
        LoginWithProvider
      } else LoginWithoutProvider
    }
  }

  // An example of how to take screenshots during a run to get better debugging information
  // def debugLogInWith(userCredentials: UserCredentials, spec: AcceptanceSpec)(implicit webDriver: WebDriver): LoginType =
  //   eventually {
  //     if (userCredentials.provider.value != "") {
  //       println("use provider")
  //       oidcButton.click() sleep (5 seconds)
  //       val providerLoginPage = ProviderLoginPage(userCredentials)
  //       if (!(currentUrl startsWith providerLoginPage.url)) {
  //         println("Not on provider")
  //         spec.writeScreenshot()
  //         oidcButton.click() sleep (5 seconds)
  //         spec.writeScreenshot()
  //       }
  //       providerLoginPage.logIn sleep (5 seconds)
  //       LoginWithProvider
  //     } else {
  //       usernameField.clear() sleep (1 second)
  //       usernameField.sendKeys(userCredentials.email.value) sleep (1 second)

  //       passwordField.clear() sleep (1 second)
  //       passwordField.sendKeys(userCredentials.password.value) sleep (1 second)

  //       logInButton.click()

  //       val providerLoginPage = ProviderLoginPage(userCredentials)
  //       if (currentUrl startsWith providerLoginPage.url) {
  //         providerLoginPage.logIn
  //         LoginWithProvider
  //       } else LoginWithoutProvider
  //     }
  //   }

  private def usernameField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#username")) getOrElse fail("Username field not found")
  }

  private def passwordField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#password")) getOrElse fail("Password field not found")
  }

  private def oidcButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("a.zocial.oidc")) getOrElse fail("OpenIDConnector Button not found")
  }

  def logInButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(".btn.btn-primary.btn-block.btn-lg")) getOrElse fail("Log In button not found")
  }
}
