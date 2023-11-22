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

import cats.syntax.all._
import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, GitLabApi, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows.Login
import org.scalacheck.Gen
import org.scalatest.OptionValues
import tooling.KnowledgeGraphModel._

import scala.concurrent.duration._

class ProjectAPISpec extends AcceptanceSpec with Login with KnowledgeGraphApi with GitLabApi with OptionValues {

  scenario("User can do CRUD operations on a project using the API") {

    `verify user has GitLab credentials`

    When("the user creates a new Project")
    val namespaceId = `find user namespace ids`.headOption.getOrElse(fail("No namespaces found"))
    val newProject  = NewProject.generate(namespaceId, ProjectTemplate.pythonMinimal, Image.wheelPngExample)
    val slug        = `POST /knowledge-graph/projects`(newProject)

    `wait for KG to process events`(slug, webDriver)

    Then("the user should be able to get details of it")
    val afterCreation = `GET /knowledge-graph/projects/:slug`(slug).value
    newProject.visibility                      shouldBe afterCreation.visibility
    newProject.maybeDescription                shouldBe afterCreation.maybeDescription
    newProject.keywords                        shouldBe afterCreation.keywords
    newProject.maybeImage.map(_.toName).toList shouldBe afterCreation.images.map(_.toName)

    When("the user updates the project")
    val newDesc       = projects.Description.generate().some
    val newKeywords   = Set(projects.Keyword.generate())
    val newVisibility = Gen.oneOf(projects.Visibility.all).suchThat(_ != newProject.visibility).generateOne
    val newImage      = Image.bikeJpgExample.some
    val updates       = ProjectUpdates(newDesc.some, newKeywords.some, newVisibility.some, newImage.some)
    `PATCH /knowledge-graph/projects/:slug`(slug, updates)

    `wait for KG to process events`(slug, webDriver)

    Then("the API should return the updated values")
    val afterUpdate = `GET /knowledge-graph/projects/:slug`(slug).value
    newVisibility                 shouldBe afterUpdate.visibility
    newDesc                       shouldBe afterUpdate.maybeDescription
    newKeywords                   shouldBe afterUpdate.keywords
    newImage.map(_.toName).toList shouldBe afterUpdate.images.map(_.toName)

    When("the user removes the project")
    `DELETE /knowledge-graph/projects/:slug`(slug) sleep (5 seconds)

    Then("the API should not be able to find the project any more")
    `GET /knowledge-graph/projects/:slug`(slug) shouldBe None
  }
}
