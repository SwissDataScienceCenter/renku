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

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectUrl
import ch.renku.acceptancetests.tooling.AcceptanceSpec

trait Settings {
  self: AcceptanceSpec with Project =>

  def `set project tags`: Unit = {

    `try few times before giving up` { _ =>
      When("the user navigates to the Settings tab")
      click on projectPage.Settings.tab
    }

    And("they add some tags")
    val tags = "automated-test"
    projectPage.Settings addProjectTags tags

    `try few times before giving up` { _ =>
      Then("the tags should be added")
      verify that projectPage.Settings.projectTags hasValue tags
    }
  }

  def `set project description`: Unit = {
    When("the user set the Project Description")
    val gitlabDescription = "GitLab description"
    projectPage.Settings updateProjectDescription gitlabDescription

    `try few times before giving up` { _ =>
      And("they navigate to the Overview tab")
      click on projectPage.Overview.tab

      Then("they should see the updated project description")
      verify that projectPage.Overview.projectDescription contains gitlabDescription
    }
  }

  def `find project Http URL in the Settings Page`: ProjectUrl = {

    `try few times before giving up` { _ =>
      When("the user navigates to the Settings tab")
      click on projectPage.Settings.tab
    }

    Then("the user can find the project Http Url")
    ProjectUrl(projectPage.Settings.projectHttpUrl.getText)
  }
}
