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

package ch.renku.acceptancetests

import ch.renku.acceptancetests.tooling.console.{CommandOps, rootWorkDirectory}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, console}
import ch.renku.acceptancetests.workflows._

import java.nio.file.Path

/** Login to Renku from CLI.
  */
class CliLoginSpec extends AcceptanceSpec with CLIConfiguration with Login with Project with Settings {

  Scenario("User can log in from CLI") {

    // TODO Make sure that a browser exists in the image -> probably true or otherwise ChromeDriver won't run
    // TODO Define a 'BROWSER' env var with possible chrome values so that webbrowser module picks up the right one:
    // export BROWSER=google-chrome:google-chrome-stable:chrome:chromium:chromium-browser

    `setup renku CLI`

    `log in to Renku from CLI`

    When("Reading renku token from the global config file")
    val token = `read renku token`
    Then("Token has a value")
    assert(token.trim.nonEmpty, "Token is empty")

    // Uncomment these lines once https://github.com/SwissDataScienceCenter/renku-python/issues/2472 is deployed
    //When("User logs out of Renku from CLI")
    //`log out of Renku from CLI`()
    //And("Reading renku token from the global config file")
    //token = `read renku token`
    //Then("Token is empty")
    //assert(token.trim.isEmpty, "Token is not empty: $token")
  }

  Scenario("User can log in from CLI in a Renku project") {
    `setup renku CLI`

    `log in to Renku`

    `create, continue or open a project`

    val projectUrl = `find project Http URL in the Settings Page`
    implicit val projectDirectory: Path = `clone and migrate a project`(projectUrl)

    `log in to Renku from CLI project`
  }

  private def `read renku token`: String = {
    implicit val workFolder: Path = rootWorkDirectory

    console %%> c"renku config show http.$renkuBaseUrl"
  }
}
