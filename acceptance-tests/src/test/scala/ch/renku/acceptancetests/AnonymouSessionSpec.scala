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

import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnv}
import ch.renku.acceptancetests.workflows._

class AnonymouSessionSpec extends AcceptanceSpec with Project with AnonEnv with Login {

  scenario("User can launch session") {

    `log in to Renku`

    `create, continue or open a project`

    `log out of Renku`

    `launch anonymous session`(projectPage, projectDetails).map(_ => `stop session`(projectPage))
  }
}
