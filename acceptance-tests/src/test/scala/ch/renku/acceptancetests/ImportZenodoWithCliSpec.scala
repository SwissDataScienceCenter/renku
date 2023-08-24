/*
 * Copyright 2023 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.pages.DatasetPage
import ch.renku.acceptancetests.tooling._
import ch.renku.acceptancetests.tooling.console._
import ch.renku.acceptancetests.workflows._

import java.nio.file.Path
import scala.concurrent.duration._

class ImportZenodoWithCliSpec
    extends AcceptanceSpec
    with Environments
    with Login
    with Project
    with Settings
    with JupyterNotebook
    with CLIConfiguration
    with Datasets
    with KnowledgeGraphApi {

  scenario("User can import a Dataset from Zenodo") {

    `log in to Renku`

    `setup renku CLI`

    `create, continue or open a project`

    val projectUrl = `find project Http URL in the Overview Page`

    implicit val projectFolder: Path = createTempFolder

    `setup git configuration`

    When("the user clones the project locally")
    console %> c"git clone ${projectUrl add authorizationToken} $projectFolder"

    And("migrates the project")
    console %> c"renku migrate"

    And("enables git lfs")
    console %> c"git lfs install --local"

    And("imports a dataset from Zenodo")
    console %> c"renku dataset import 10.5281/zenodo.7822477"
      .userInput("y")

    And("pushes all the commits to the remote")
    console %> c"git push"

    sleep(10 seconds)

    When("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectDetails.asProjectIdentifier, webDriver)

    val dsPage = DatasetPage(DatasetName("biodiversity_and_healthy"))
    `navigate to the dataset`(dsPage)

    dsPage.datasetTitle.getText should include("Strava Metro")

    `log out of Renku`
  }
}
