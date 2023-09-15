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

import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows._

import java.lang.System.getProperty
import java.time.{Instant, Duration => JDuration}
import Ordering.Implicits._
import scala.util.matching.Regex

/** Delete old projects.
  *
  * During testing, test projects can accumulate and they are tedious to clean up. This spec
  * will delete all projects that match a pattern and can be used to batch delete projects
  * from failed test runs, for example.
  *
  * By default, it removes projects matching the test project pattern name and are older than a week.
  */
class BatchProjectRemovalSpec extends AcceptanceSpec with Login with RemoveProject with KnowledgeGraphApi {

  scenario("User can delete many projects project") {

    if (batchRemoveConfig.batchRemove) `login and remove projects`
    else Given("specifically asked to not remove projects")
  }

  def `login and remove projects`: Unit = {

    `log in to Renku`

    `find and remove projects`

    `log out of Renku`
  }

  private def `find and remove projects`: Unit =
    `get user's projects from GitLab` foreach { case ProjectInfo(_, path, fullPath, created) =>
      Given(s"project $path found")

      val days = JDuration.ofDays(7)
      if (batchRemoveConfig.patterns.exists(_ matches path) && (created < Instant.now().minus(days))) {
        And(s"the project matches the removal pattern and it's older than ${days.toDays} days")
        Then("the project is removed")
        `DELETE /knowledge-graph/projects/:slug`(projects.Slug(fullPath))
      } else {
        And(s"the project doesn't match the removal pattern or it's not older than ${days.toDays} days")
        Then("project is left untouched")
      }
    }

  private case class BatchRemoveConfig(batchRemove: Boolean, patterns: List[Regex])

  private lazy val batchRemoveConfig: BatchRemoveConfig = {

    val batchRemove =
      Option(getProperty("batchRem"))
        .orElse(sys.env.get("RENKU_TEST_BATCH_REMOVE"))
        .flatMap(_.toBooleanOption)
        .getOrElse(true)

    val defaultPatterns = List(
      "test-(\\d{4})-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+).*",
      "test-session-.*"
    )

    val projectNamePatterns = Option(getProperty("remPattern"))
      .orElse(sys.env.get("RENKU_TEST_REMOVE_PATTERN"))
      .map(List(_))
      .getOrElse(defaultPatterns)

    BatchRemoveConfig(batchRemove, projectNamePatterns.map(_.r))
  }
}
