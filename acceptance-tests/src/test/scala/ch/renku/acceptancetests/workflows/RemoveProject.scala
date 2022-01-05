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

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectIdentifier
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, GitLabApi}

import scala.concurrent.duration._

trait RemoveProject extends BrowserNavigation {
  self: AcceptanceSpec with GitLabApi =>

  def `remove project in GitLab`(implicit projectId: ProjectIdentifier): Unit = {
    When(s"the '${projectId.slug}' project is removed")
    `delete project in GitLab`(projectId)
    sleep(1 second)
  }
}
