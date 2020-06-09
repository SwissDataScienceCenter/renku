package ch.renku.acceptancetests.workflows

import java.nio.file.Path

import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.console
import ch.renku.acceptancetests.tooling.console._
import org.scalatest.{FeatureSpec, GivenWhenThen}

trait CLIConfiguration extends GivenWhenThen {

  self: FeatureSpec =>

  def `setup git configuration`(implicit workfolder: Path, userCredentials: UserCredentials): Unit = {
    When("the user has configured its git username and email")
    val configuredUsername = console %%> c"git config --global user.name"
    if (configuredUsername.trim.isEmpty) console %> c"git config --global user.name ${userCredentials.fullName}"

    val configuredEmail: String = console %%> c"git config --global user.email"
    if (configuredEmail.trim.isEmpty) console %> c"git config --global user.email ${userCredentials.email}"
  }
}
