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

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnv, DocsScreenshots}
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages.ProjectPage

import scala.concurrent.duration._
import scala.language.postfixOps

class MinimalFunctionalitySpec
    extends AcceptanceSpec
    with AnonEnv
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with Fork {

  scenario("User can use basic functionality of Renku") {

    implicit val loginType: LoginType = `log in to Renku`

    implicit val projectDetails: ProjectDetails = ProjectDetails.generate()

    createNewProject(projectDetails)

    verifyMergeRequestsIsEmpty
    verifyIssuesIsEmpty
    createNewIssue

    addChangeToProject
    createNewMergeRequest

    setProjectTags
    setProjectDescription

    forkTestCase

    go to ProjectPage()
    `remove project in GitLab`(projectDetails)
    `verify project is removed`

    launchUnprivilegedEnvironment
    stopEnvironment(anonEnvConfig.projectId)

    `log out of Renku`

    launchAnonymousEnvironment map (_ => stopEnvironment(anonEnvConfig.projectId))

  }

  def addChangeToProject(implicit projectDetails: ProjectDetails): Unit = {
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser) {
      override lazy val captureScreenshots: Boolean = false
    }
    val jupyterLabPage = launchEnvironment

    When("the user clicks on the Terminal icon")
    click on jupyterLabPage.terminalIcon sleep (2 seconds)
    createBranchInJupyterLab(jupyterLabPage)

    stopEnvironment
  }
}
