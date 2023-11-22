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

import ch.renku.acceptancetests.tooling.{AcceptanceSpec, GitLabApi, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows._

/** Run the HandsOn from the documentation.
  */
class HandsOnSpec
    extends AcceptanceSpec
    with Login
    with Project
    with Settings
    with FlightsTutorial
    with Datasets
    with GitLabApi
    with KnowledgeGraphApi {

  scenario("User can do hands-on tutorial") {

    `verify user has GitLab credentials`

    `setup renku CLI`

    val projectSlug = `create or use extant project`

    val projectUrl     = `get repo Http URL`(projectSlug)
    val flightsDataset = `follow the flights tutorial`(projectUrl)

    When("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectSlug, webDriver)

    `GET /knowledge-graph/projects/:slug/datasets`(projectSlug) shouldBe List(flightsDataset)

    val res =
      `GET /knowledge-graph/projects/:slug/files/:path/lineage`(projectSlug, "notebooks/01-CountFlights.ran.ipynb")
    println(res)

    res.isEmpty shouldBe false

    `log out of Renku`
  }
}
