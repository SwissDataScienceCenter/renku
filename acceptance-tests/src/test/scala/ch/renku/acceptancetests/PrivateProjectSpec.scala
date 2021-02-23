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

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Visibility}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, DocsScreenshots, KnowledgeGraphApi}
import ch.renku.acceptancetests.workflows.{BrowserNavigation, Collaboration, Datasets, Environments, JupyterNotebook, Login, LoginType, NewProject, RemoveProject, Settings}

class PrivateProjectSpec
    extends AcceptanceSpec
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with JupyterNotebook
    with Datasets
    with KnowledgeGraphApi
    with BrowserNavigation {

  scenario("User can launch Jupyter notebook when the project is private") {
    implicit val docsScreenshots: DocsScreenshots = new DocsScreenshots(this, browser)
    implicit val projectDetails:  ProjectDetails  = ProjectDetails.generate(visibility = Visibility.Private)

    `log in to Renku`
    createNewProject(projectDetails)
    verifyUserCanWorkWithJupyterNotebook
    `remove project in GitLab`(projectDetails)
    switchToRenkuTab
    `log out of Renku`
  }
}
