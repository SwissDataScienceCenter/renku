package ch.renku.acceptancetests.workflows

import java.nio.file.Path

import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.console._
import ch.renku.acceptancetests.tooling.{RenkuCliConfig, console}
import org.scalatest.{FeatureSpec, GivenWhenThen}

trait CLIConfiguration extends GivenWhenThen {

  self: FeatureSpec =>

  def `setup git configuration`(implicit userCredentials: UserCredentials): Unit = {
    implicit val workFolder: Path = rootWorkDirectory

    When("the user has configured its git username and email")
    val configuredUsername = console %%> c"git config --global user.name"
    if (configuredUsername.trim.isEmpty) console %> c"git config --global user.name '${userCredentials.fullName}'"

    val configuredEmail: String = console %%> c"git config --global user.email"
    if (configuredEmail.trim.isEmpty) console %> c"git config --global user.email '${userCredentials.email}'"
  }

  def `verify renku version`(implicit cliConfig: RenkuCliConfig, userCredentials: UserCredentials): Unit = {
    implicit val workFolder: Path = rootWorkDirectory

    When(s"the user has renku v${cliConfig.version} installed")
    val installedRenkuVersion = console %%> c"renku --version"
    if (installedRenkuVersion.trim != cliConfig.version.toString)
      console %> c"${cliConfig.installCommand.toString.format(cliConfig.version.toString)}"
  }
}
