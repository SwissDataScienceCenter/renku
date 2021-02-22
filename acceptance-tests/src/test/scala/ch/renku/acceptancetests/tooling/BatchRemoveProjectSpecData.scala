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

/**
  * Configuration specific to the BatchRemove spec.
  */
trait BatchRemoveProjectSpecData {

  case class BatchRemoveConfig(batchRemove: Boolean = false,
                               pattern:     String = "test-(\\d{4})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})"
  )

  protected lazy val batchRemoveConfig: Option[BatchRemoveConfig] = {

    val batchRemove = Option(getProperty("batchRem")) orElse sys.env.get("RENKU_TEST_BATCH_REMOVE") match {
      case Some(s) => Some(s.toBoolean)
      case None    => None
    }
    val projectNamePattern = Option(getProperty("remPattern")) orElse sys.env.get("RENKU_TEST_REMOVE_PATTERN");

    batchRemove match {
      case Some(b) =>
        projectNamePattern match {
          case Some(p) => Some(BatchRemoveConfig(b, p))
          case None    => Some(BatchRemoveConfig(b))
        }
      case None => None
    }
  }
}
