/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.CliVersion
import ch.renku.acceptancetests.model.CliVersion.{NonReleasedVersion, ReleasedVersion}
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.console._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, console}

import java.nio.file.Path

trait CLIConfiguration {
  self: AcceptanceSpec =>

  def `setup renku CLI`: Unit = {
    implicit val workFolder: Path = rootWorkDirectory

    if (apiCliVersion.value != cliVersion.value)
      logger.warn(s"Local Renku CLI is is going to use v$cliVersion while Renku API is on v$apiCliVersion")

    CliVersion.get(console %%> c"renku --version") match {
      case Right(`cliVersion`) =>
        Given(s"renku CLI $cliVersion installed")
      case _ =>
        Given(s"there's no Renku CLI $cliVersion installed")
        console %%> c"python3 -m pip uninstall --yes renku"
        cliVersion match {
          case version: ReleasedVersion =>
            Then(s"the user installs released Renku CLI v$cliVersion")
            console %> c"python3 -m pip install 'renku==$version'"
          case version: NonReleasedVersion =>
            Then(s"the user installs Renku CLI v$cliVersion from source")
            console %> c"python3 -m pip install git+https://github.com/SwissDataScienceCenter/renku-python.git@${version.commitSha}"
        }
    }
  }

  def `setup git configuration`(implicit userCredentials: UserCredentials): Unit = {
    implicit val workFolder: Path = rootWorkDirectory

    And("the user has configured its git username and email")
    val configuredUsername = console %%> c"git config --global user.name"
    if (configuredUsername.trim.isEmpty) console %> c"git config --global user.name '${userCredentials.fullName}'"

    val configuredEmail: String = console %%> c"git config --global user.email"
    if (configuredEmail.trim.isEmpty) console %> c"git config --global user.email '${userCredentials.email}'"
  }
}
