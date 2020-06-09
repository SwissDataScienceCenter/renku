package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.GitLabPages.GitLabBaseUrl
import ch.renku.acceptancetests.pages.RenkuPage.RenkuBaseUrl
import ch.renku.acceptancetests.workflows.LoginType
import ch.renku.acceptancetests.workflows.LoginType.{LoginWithProvider, LoginWithoutProvider}
import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string.Url

trait AcceptanceSpecData {

  private val testsDefaults = TestsDefaults()

  protected implicit lazy val renkuBaseUrl: RenkuBaseUrl = {
    val env = Option(getProperty("env")) orElse sys.env.get("RENKU_TEST_URL") orElse testsDefaults.env
    toBaseUrl(env.get) getOrElse showErrorAndStop(
      "-Denv argument or RENKU_TEST_URL environment variable is not a valid URL"
    )
  }

  private lazy val toBaseUrl: String => Either[String, RenkuBaseUrl] = (url) => {
    val baseUrl = if (url.endsWith("/")) url.substring(0, url.length - 1) else url;
    RefType
      .applyRef[String Refined Url](baseUrl)
      .map(RenkuBaseUrl.apply)
  }

  protected implicit lazy val userCredentials: UserCredentials = {
    for {
      email    <- Option(getProperty("email")) orElse sys.env.get("RENKU_TEST_EMAIL") orElse testsDefaults.email flatMap toNonEmpty
      username <- Option(getProperty("username")) orElse sys.env.get("RENKU_TEST_USERNAME") orElse testsDefaults.username flatMap toNonEmpty
      password <- Option(getProperty("password")) orElse sys.env.get("RENKU_TEST_PASSWORD") orElse testsDefaults.password flatMap toNonEmpty
      fullName <- Option(getProperty("fullname")) orElse sys.env.get("RENKU_TEST_FULL_NAME") orElse testsDefaults.fullname flatMap toNonEmpty
      useProvider = Option(getProperty("provider")) orElse sys.env.get("RENKU_TEST_PROVIDER") match {
        case Some(s) => s.nonEmpty
        case None    => false
      }
      register = Option(getProperty("register")) orElse sys.env.get("RENKU_TEST_REGISTER") match {
        case Some(s) => s.nonEmpty
        case None    => false
      }
    } yield UserCredentials(email, username, password, fullName, useProvider, register)
  } getOrElse showErrorAndStop(
    "You must provide either the arguments -Dusername -Dfullname, -Demail or/and -Dpassword args invalid or missing" +
      " or set the environment variables RENKU_TEST_EMAIL RENKU_TEST_USERNAME RENKU_TEST_PASSWORD and/or RENKU_TEST_FULL_NAME"
  )

  private lazy val toNonEmpty: String => Option[String Refined NonEmpty] =
    RefType
      .applyRef[String Refined NonEmpty](_)
      .toOption

  private def showErrorAndStop[T](message: String Refined NonEmpty): T = {
    Console.err.println(message)
    System.exit(1)
    throw new IllegalArgumentException(message)
  }

  protected implicit def gitLabBaseUrlFrom(implicit loginType: LoginType, renkuBaseUrl: RenkuBaseUrl): GitLabBaseUrl =
    loginType match {
      case LoginWithProvider    => gitLabProviderBaseUrl(renkuBaseUrl.value)
      case LoginWithoutProvider => GitLabBaseUrl(renkuBaseUrl.value)
    }

  private def gitLabProviderBaseUrl(baseUrl: String): GitLabBaseUrl =
    if (baseUrl.endsWith("dev.renku.ch"))
      GitLabBaseUrl("https://dev.renku.ch")
    else
      GitLabBaseUrl("https://renkulab.io")

  implicit lazy val renkuCliConfig: RenkuCliConfig = RenkuCliConfig(
    version = Option(getProperty("renkuVersion"))
      .orElse(sys.env.get("RENKU_TEST_CLI_VERSION"))
      .orElse(testsDefaults.renkuVersion)
      .map(RenkuVersion)
      .getOrElse(showErrorAndStop("No renku cli version found")),
    installCommand = RenkuInstallCommand(testsDefaults.renkuInstallCommand)
  )
}
