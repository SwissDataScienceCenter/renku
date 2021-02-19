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

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.AcceptanceSpec

import scala.language.postfixOps

trait Fork {
  self: AcceptanceSpec with RemoveProject =>

  def forkTestCase(implicit projectDetails: ProjectDetails, loginType: LoginType): Unit = {
    val projectPage = ProjectPage()
    go to projectPage
    val forkedProject = forkProject
    `remove project in GitLab`(forkedProject.projectDetails)
    `verify project is removed`(forkedProject.projectDetails)
  }

  def forkProject(implicit projectDetails: ProjectDetails): ForkedProject = {
    val projectPage = ProjectPage()
    When("user clicks on the fork button")
    click on projectPage.forkButton

    val forkedProjectDetails: ProjectDetails = ProjectDetails.generate()

    And(s"fills in the title (${forkedProjectDetails.title}) and submits")
    projectPage.ForkDialog.submitFormWith(forkedProjectDetails)

    Then("the project gets forked and the project page gets displayed")
    val forkedProjectPage = ProjectPage()(forkedProjectDetails, implicitly)
    verify browserAt forkedProjectPage

    new ForkedProject(forkedProjectDetails)
  }

  class ForkedProject(val projectDetails: ProjectDetails) {}
}
