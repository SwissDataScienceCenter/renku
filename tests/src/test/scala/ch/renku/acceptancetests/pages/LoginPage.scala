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
  case class ProviderLoginPage()
      extends selenium.Page
      with ScalatestMatchers
      with Eventually
      with AcceptanceSpecPatience {

    final override lazy val url: String = "https://dev.renku.ch/auth/realms/Renku/protocol/openid-connect/auth"

    def logInWith(userCredentials: UserCredentials)(implicit webDriver: WebDriver): Unit = eventually {
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

  case class UpdateAccountInfoPage(userCredentials: UserCredentials)
      extends selenium.Page
      with ScalatestMatchers
      with Eventually
      with AcceptanceSpecPatience {

    // N.b. This is not the correct URL, but just here to get the compiler to not complain
    final override lazy val url: String = "https://dev.renku.ch/auth/realms/Renku/login-actions/first-broker-login"

    def updateInfo(implicit webDriver: WebDriver): Unit = eventually {
      emailField.clear() sleep (1 second)
      emailField.sendKeys(userCredentials.email.value) sleep (1 second)

      val nameComps = userCredentials.fullName.split(" ")
      val firstName = nameComps.head
      val lastName  = nameComps.last

      firstNameField.clear() sleep (1 second)
      firstNameField.sendKeys(firstName) sleep (1 second)

      lastNameField.clear() sleep (1 second)
      lastNameField.sendKeys(lastName) sleep (1 second)

      submitButton.click()
    }

    def emailField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#email")) getOrElse fail("Email field not found")
    }

    def firstNameField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#firstName")) getOrElse fail("First name field not found")
    }

    def lastNameField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#lastName")) getOrElse fail("Last name field not found")
    }

    def submitButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("#kc-form-buttons > input")) getOrElse fail("Submit button not found")
    }
  }

  case class RegisterNewUserPage()
      extends selenium.Page
      with ScalatestMatchers
      with Eventually
      with AcceptanceSpecPatience {

    final override lazy val url: String = "https://dev.renku.ch/auth/realms/Renku/login-actions/registration"

    def registerNewUserWith(userCredentials: UserCredentials)(implicit webDriver: WebDriver): Unit = eventually {
      val nameComps = userCredentials.fullName.split(" ")
      val firstName = nameComps.head
      val lastName  = nameComps.last

      firstNameField.clear() sleep (1 second)
      firstNameField.sendKeys(firstName) sleep (1 second)

      lastNameField.clear() sleep (1 second)
      lastNameField.sendKeys(lastName) sleep (1 second)

      emailField.clear() sleep (1 second)
      emailField.sendKeys(userCredentials.email.value) sleep (1 second)

      passwordField.clear() sleep (1 seconds)
      passwordField.sendKeys(userCredentials.password.value) sleep (3 seconds)

      confirmPasswordField.clear() sleep (1 second)
      confirmPasswordField.sendKeys(userCredentials.password.value) sleep (3 seconds)

      registerButton.click()
    }

    def emailField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#email")) getOrElse fail("Email field not found")
    }

    def firstNameField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#firstName")) getOrElse fail("First name field not found")
    }

    def lastNameField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#lastName")) getOrElse fail("Last name field not found")
    }

    def passwordField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#password")) getOrElse fail("Password field not found")
    }

    def confirmPasswordField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#password-confirm")) getOrElse fail("Confirm Password field not found")
    }

    def registerButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("#kc-form-buttons > input")) getOrElse fail("Register button not found")
    }
  }

  case class AuthorizeApplicationPage()
      extends selenium.Page
      with ScalatestMatchers
      with Eventually
      with AcceptanceSpecPatience {

    final override lazy val url: String = "https://dev.renku.ch/gitlab/oauth/authorize"

    def authorize(implicit webDriver: WebDriver): Unit =
      authorizeButton.click()

    def authorizeButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(
        cssSelector(
          "#content-body > main > div > div > div.modal-body > div > form:nth-child(2) > input.btn.btn-success.prepend-left-10"
        )
      ) getOrElse fail("Authorize button not found")
    }
  }

  def logInWith(userCredentials: UserCredentials)(implicit webDriver: WebDriver): Unit = eventually {
    usernameField.clear() sleep (1 second)
    usernameField.sendKeys(userCredentials.email.value) sleep (1 second)

    passwordField.clear() sleep (1 second)
    passwordField.sendKeys(userCredentials.password.value) sleep (1 second)

    logInButton.click()
  }

  private def usernameField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#username")) getOrElse fail("Username field not found")
  }

  private def passwordField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#password")) getOrElse fail("Password field not found")
  }

  private def oidcButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("a.zocial.oidc")) getOrElse fail("OpenIDConnector Button not found")
  }

  private def registerLink(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#kc-registration > span > a")) getOrElse fail("Register link not found")
  }

  def openProviderLoginPage(implicit webDriver: WebDriver): ProviderLoginPage = {
    oidcButton.click() sleep (5 seconds)
    val providerLoginPage = ProviderLoginPage()
    if (!(currentUrl startsWith providerLoginPage.url)) {
      println("Not on OpenID login provider page, trying again")
      oidcButton.click() sleep (5 seconds)
    }
    return providerLoginPage
  }

  def openRegistrationForm(implicit webDriver: WebDriver): LoginType = {
    registerLink.click() sleep (5 seconds)
    if (currentUrl contains "/auth/realms/Renku/protocol/openid-connect/auth") {
      // button didn't work, try again
      registerLink.click() sleep (5 seconds)
    }

    LoginWithProvider
  }

  def loginSucceeded(implicit webDriver: WebDriver): Boolean = eventually {
    find(cssSelector("#kc-content-wrapper > div.alert.alert-error")) match {
      // If we find the alert, login failed!
      case Some(_) => false
      case None    => true
    }
  }

  def logInButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(".btn.btn-primary.btn-block.btn-lg")) getOrElse fail("Log In button not found")
  }
}
