/*
 * Copyright 2021 Swiss Data Science Center (SDSC)
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

import java.nio.file.Path

import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.console
import ch.renku.acceptancetests.tooling.console._
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
}
