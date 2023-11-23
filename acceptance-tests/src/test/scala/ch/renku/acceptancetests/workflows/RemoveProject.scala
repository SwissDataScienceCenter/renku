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

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}

import scala.concurrent.duration._

trait RemoveProject extends BrowserNavigation {
  self: AcceptanceSpec with KnowledgeGraphApi =>

  def `remove project in GitLab`(slug: projects.Slug): Unit = {
    And(s"the '$slug' project is removed")
    `DELETE /knowledge-graph/projects/:slug`(slug)
    sleep(1 second)
  }
}
