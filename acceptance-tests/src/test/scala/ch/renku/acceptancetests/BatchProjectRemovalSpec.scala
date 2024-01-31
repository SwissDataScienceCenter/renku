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

package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.tooling.TestLogger.logger
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

  private val gracePeriod = JDuration.ofDays(7)

  scenario("Delete old user's test projects") {

    if (batchRemoveConfig.batchRemove) `remove projects`
    else Given("specifically asked to not remove projects")
  }

  private def `remove projects`: Unit = {

    `verify user has GitLab credentials`

    val summary = `find and remove projects`

    logger.info(s"Removing summary: $summary")
  }

  private def `find and remove projects` =
    `get user's projects from GitLab`.foldLeft(Summary.empty) {
      case (summary, ProjectInfo(_, path, fullPath, created)) =>
        if (batchRemoveConfig.patterns.exists(_ matches path) && (created < Instant.now().minus(gracePeriod))) {
          logger.info(s"Removing '$path' - the removal pattern matches and it's older than ${gracePeriod.toDays} days")
          `DELETE /knowledge-graph/projects/:slug`(projects.Slug(fullPath))
          summary.incrementRemoved()
        } else
          summary.incrementFound()
    }

  private case class Summary(found: Int, removed: Int) {
    def incrementFound():   Summary = copy(found = found + 1)
    def incrementRemoved(): Summary = incrementFound().copy(removed = removed + 1)

    override lazy val toString = s"found: $found, removed: $removed"
  }
  private object Summary {
    val empty: Summary = Summary(0, 0)
  }

  private case class BatchRemoveConfig(batchRemove: Boolean, patterns: List[Regex])

  private lazy val batchRemoveConfig: BatchRemoveConfig = {

    val batchRemove =
      (Option(getProperty("batchRem")) orElse sys.env.get("RENKU_TEST_BATCH_REMOVE"))
        .flatMap(_.toBooleanOption)
        .getOrElse(true)

    val defaultPatterns = List(
      "test-(\\d{4})-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+).*",
      "test-session-.*",
      "cypress-.*"
    )

    val projectNamePatterns =
      (Option(getProperty("remPattern")) orElse sys.env.get("RENKU_TEST_REMOVE_PATTERN"))
        .map(List(_))
        .getOrElse(defaultPatterns)

    BatchRemoveConfig(batchRemove, projectNamePatterns.map(_.r))
  }
}
