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
import ch.renku.acceptancetests.model.projects.ProjectIdentifier
import eu.timepit.refined.api.Refined
case class AnonEnvConfig(projectId: ProjectIdentifier, isAvailable: Boolean = false)

/**
  * Configuration for the anonymous environment
  */
trait AnonEnv extends AcceptanceSpecData {
  protected implicit lazy val anonEnvConfig: AnonEnvConfig =
    AnonEnvConfig(anonProjectIdentifier, isAnonEnvAvailable)
  protected lazy val anonProjectIdentifier: ProjectIdentifier = {
    Option(getProperty("anon")) orElse sys.env.get("RENKU_TEST_ANON_PROJECT") match {
      case Some(s) =>
        val projectIdComponents = s.split("/").map(_.trim).toList
        projectIdComponents match {
          case username :: tail =>
            tail.headOption match {
              case Some(projectName) =>
                ProjectIdentifier(Refined.unsafeApply(username), Refined.unsafeApply(projectName))
              case None => defaultProjectIdentifier
            }
          case _ => defaultProjectIdentifier
        }
      case None => defaultProjectIdentifier
    }
  }
  private val defaultProjectIdentifier =
    ProjectIdentifier(Refined.unsafeApply("andi"), Refined.unsafeApply("public-test-project"))
  protected lazy val isAnonEnvAvailable: Boolean = {
    val baseUrl = renkuBaseUrl
    Option(getProperty("anonAvail")) orElse sys.env.get("RENKU_TEST_ANON_AVAILABLE") match {
      case Some(s) =>
        s.toLowerCase == "true"
      case None =>
        baseUrl.value.value.equals("https://dev.renku.ch")
    }
  }
}
