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

package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Template, Visibility}
import eu.timepit.refined.api.Refined

/**
  * Configuration specific to the ExistingProject spec.
  */
trait ExistingProjectSpecData {

  protected lazy val existingProjectDetails: Option[ProjectDetails] = {
    Option(getProperty("extant")) orElse sys.env.get("RENKU_TEST_EXTANT_PROJECT") match {
      case Some(readMeTitle) =>
        Some(
          ProjectDetails(Refined.unsafeApply(readMeTitle),
                         Visibility.Public,
                         Refined.unsafeApply("unused"),
                         Template(Refined.unsafeApply("Not used")),
                         readMeTitle
          )
        )
      case None => None
    }
  }
}
