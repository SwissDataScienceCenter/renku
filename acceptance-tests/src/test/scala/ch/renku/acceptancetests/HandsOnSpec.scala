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

import scala.concurrent.duration._

/** Run the HandsOn from the documentation. */
class HandsOnSpec
    extends AcceptanceSpec
    with Login
    with Project
    with FlightsTutorial
    with GitLabApi
    with KnowledgeGraphApi {

  scenario("User can do hands-on tutorial") {

    `verify user has GitLab credentials`

    `setup renku CLI`

    val projectSlug    = `create or use extant project`
    val projectUrl     = `get repo Http URL`(projectSlug)
    val flightsDataset = `follow the flights tutorial`(projectUrl)

    // to give time the push event is sent by GL and processed on the KG side
    sleep(10 seconds)

    When("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectSlug, webDriver)

    Then(s"the '$flightsDataset' dataset should exist on the project")
    `GET /knowledge-graph/projects/:slug/datasets`(projectSlug) shouldBe List(flightsDataset)

    val file = "notebooks/01-CountFlights.ran.ipynb"
    And(s"lineage for the '$file' should exist as well")
    `GET /knowledge-graph/projects/:slug/files/:path/lineage`(projectSlug, file) shouldBe a[Some[_]]
  }
}
