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

package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects._
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import ch.renku.acceptancetests.workflows._

import java.lang.System.getProperty
import scala.concurrent.duration._
import scala.util.matching.Regex

/**
  * Delete many projects.
  *
  * During testing, test projects can accumulate and they are tedious to clean up. This spec
  * will delete all projects that match a pattern and can be used to batch delete projects
  * from failed test runs, for example.
  *
  * It needs to be explicitly activated to avoid accidentally destroying projects.
  */
class BatchRemoveProjectSpec extends AcceptanceSpec with Login with RemoveProject {

  Scenario("User can delete many projects project") {
    batchRemoveConfig match {
      case Some(config) =>
        if (config.batchRemove) `login and remove projects`(config)
        else {
          Given("specifically asked to not remove projects")
          Then("do not remove anything")
        }
      case None =>
        Given("not asked to remove projects")
        Then("do not remove anything")
    }
  }

  def `login and remove projects`(config: BatchRemoveConfig): Unit = {
    `log in to Renku`

    Given("projects to remove")
    removeProjects(config)

    `log out of Renku`
  }

  private def removeProjects(config: BatchRemoveConfig): Unit = {
    When("user goes to the projects page")
    go to ProjectsPage sleep (5 seconds)
    verify browserAt ProjectsPage
    // Wait for the page to load
    val pattern: Regex = config.pattern.r
    val projectLinks = ProjectsPage.YourProjects.projectLinks
    And("lists projects to remove")
    val toRemoveLinks = projectLinks.filter { elt =>
      val href = elt getAttribute "href"
      val last = (href split "/") last;
      pattern matches last
    }
    val removeIds = toRemoveLinks map (elt => {
      val projectUrlComps = elt getAttribute "href" split "/"
      ProjectIdentifier(
        namespace = projectUrlComps(projectUrlComps.size - 2),
        slug = projectUrlComps last
      )
    })
    removeIds foreach removeProject
    go to ProjectsPage sleep (5 seconds)
  }

  private def removeProject(projectId: ProjectIdentifier): Unit = {
    // Go to the project page to get the title
    val projectPage = ProjectPage(projectId)
    go to projectPage sleep (5 seconds)
    projectPage.projectTitle match {
      case Some(s) =>
        And(s"found project $s to remove")
        Then("remove project")
        `remove project in GitLab`(projectId)
      case None =>
        And(s"could not get the title for $projectId")
        Then("do not remove")
    }
  }

  private case class BatchRemoveConfig(
      batchRemove: Boolean = false,
      pattern:     String = "test-(\\d{4})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})"
  )

  private lazy val batchRemoveConfig: Option[BatchRemoveConfig] = {

    val batchRemove = Option(getProperty("batchRem")) orElse sys.env.get("RENKU_TEST_BATCH_REMOVE") match {
      case Some(s) => Some(s.toBoolean)
      case None    => None
    }
    val projectNamePattern = Option(getProperty("remPattern")) orElse sys.env.get("RENKU_TEST_REMOVE_PATTERN")

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
