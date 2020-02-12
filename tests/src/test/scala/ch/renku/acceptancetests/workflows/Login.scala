package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import ch.renku.acceptancetests.workflows.LoginType._

import scala.concurrent.duration._
import scala.language.postfixOps

trait Login {
  self: AcceptanceSpec =>

  def logIntoRenku: LoginType = {
    Given("User is logged in on the Welcome page")
    go to LandingPage sleep (1 second)
    verify browserAt LandingPage
    click on LandingPage.loginButton
    verify browserAt LoginPage
    val loginType = LoginPage logInWith userCredentials
    verify browserAt WelcomePage
    loginType
  }

  def logOutOfRenku(implicit loginType: LoginType): Unit = {
    When("user clicks the Log out link")
    click on WelcomePage.TopBar.topRightDropDown
    click on WelcomePage.TopBar.logoutLink

    unless(loginType == LoginWithProvider) {
      Then("they should get back into the Landing page")
      verify browserAt LandingPage
      verify userCanSee LandingPage.loginButton sleep (1 second)
    }
  }
}

sealed trait LoginType

object LoginType {
  case object LoginWithProvider    extends LoginType
  case object LoginWithoutProvider extends LoginType
}
