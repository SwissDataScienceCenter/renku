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

import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.tooling.KnowledgeGraphModel.{Image, NewProject, ProjectTemplate}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import org.scalatest.Outcome

import java.lang.System.getProperty

trait ExtantProject {

  lazy val maybeExtantProjectSlug: Option[projects.Slug] =
    (Option(getProperty("extant")) orElse sys.env.get("RENKU_TEST_EXTANT_PROJECT"))
      .map(_.trim)
      .map(projects.Slug)
}

trait Project extends RemoveProject with ExtantProject with KnowledgeGraphApi {
  self: AcceptanceSpec =>

  private[Project] var maybeCreatedProject: Option[projects.Slug] = None

  def `create or use extant project`: projects.Slug =
    maybeExtantProjectSlug match {
      case Some(slug) =>
        When(s"an extant project $slug is configured")
        slug
      case None =>
        val namespaceId = `find user namespace ids`.headOption.getOrElse(fail("No namespaces found"))
        val newProject  = NewProject.generate(namespaceId, ProjectTemplate.pythonMinimal, Image.wheelPngExample)
        val slug        = `POST /knowledge-graph/projects`(newProject)
        When(s"a $slug project is created")
        maybeCreatedProject = Some(slug)
        slug
    }

  protected override type FixtureParam = Unit

  override def withFixture(test: OneArgTest): Outcome = {

    val outcome = withFixture(test.toNoArgTest((): FixtureParam))

    if (outcome.isSucceeded && maybeExtantProjectSlug.isEmpty) {
      maybeCreatedProject.foreach(`remove project in GitLab`)
    }

    outcome
  }
}
