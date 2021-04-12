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

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.pages.DatasetsPage
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows.{Datasets, DatasetsSearch, Login, Project}
import ch.renku.acceptancetests.generators.Generators._
import ch.renku.acceptancetests.generators.Generators.Implicits._

class FreeTextDatasetSearchSpec
    extends AcceptanceSpec
    with Login
    with Project
    with Datasets
    with DatasetsSearch
    with KnowledgeGraphApi {

  Scenario("User can search for datasets") {

    `log in to Renku`

    `create, continue or open a project`

    val commonWord   = nonEmptyStrings().generateOne.toLowerCase
    val dataset1Name = DatasetName.generate(containing = commonWord)
    `create a dataset`(dataset1Name)
    val dataset2Name = DatasetName.generate(containing = commonWord)
    `create a dataset`(dataset2Name)

    `search for dataset with phrase`(commonWord)

    Then("the expected datasets should be shown")
    val expectedDatasets  = List(dataset1Name, dataset2Name).sorted.map(_.toString)
    val foundDatasetLinks = DatasetsPage.searchResultLinks.map(_.getText)
    foundDatasetLinks.filter(expectedDatasets.contains) shouldBe expectedDatasets

    `log out of Renku`
  }
}
