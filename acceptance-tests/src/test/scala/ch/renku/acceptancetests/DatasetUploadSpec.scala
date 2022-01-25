/*
 * Copyright 2022 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators.paragraph
import ch.renku.acceptancetests.model.datasets.{DatasetName, DatasetTitle, FileUrl}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows._

class DatasetUploadSpec extends AcceptanceSpec with Login with Project with Datasets with KnowledgeGraphApi {

  Scenario("From the UI the user can create a dataset with an uploaded file") {

    `log in to Renku`

    `create, continue or open a project`

    When("the user wants to create a new dataset for the project")
    val originalDatasetPage = `create a dataset with uploaded file`(
      DatasetName.generate,
      FileUrl("https://raw.githubusercontent.com/SwissDataScienceCenter/renku/master/CHANGELOG.rst")
    )

    `log out of Renku`
  }
}
