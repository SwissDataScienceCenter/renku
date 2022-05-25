/*
 * Copyright 2022 Swiss Data Science Center (SDSC)
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
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnvConfig}
import org.openqa.selenium.ElementClickInterceptedException

import scala.concurrent.duration._

trait Environments {
  self: AcceptanceSpec =>

  def `launch a session`(implicit projectPage: ProjectPage): JupyterLabPage = {

    When("user clicks on the sessions tab")
    click on projectPage.Sessions.tab
    docsScreenshots.takeScreenshot()

    `click new & wait for image to build`
    docsScreenshots.takeScreenshot()

    `start session and wait until it is ready`
    docsScreenshots.takeScreenshot()

    projectPage.Sessions.Running.connectToJupyterLab

    Then("a JupyterLab page is opened on a new tab")
    val jupyterLabPage = JupyterLabPage()
    verify browserSwitchedTo jupyterLabPage sleep (5 seconds)
    jupyterLabPage
  }

  def `stop session`(implicit projectPage: ProjectPage): Unit =
    stopSessionOnProjectPage(projectPage)

  private def stopSessionOnProjectPage(projectPage: ProjectPage): Unit = {
    When("the user switches back to the Renku tab")
    verify browserSwitchedTo projectPage
    And("the sessions tab")
    click on projectPage.Sessions.tab sleep (10 seconds)
    And("they turn off the session they started before")
    click on projectPage.Sessions.Running.sessionDropdownMenu sleep (2 seconds)
    And("click on stop button")
    click on projectPage.Sessions.Running.stopButton sleep (10 seconds)
    Then("the session gets stopped and they can see the New Session link")
    verify userCanSee projectPage.Sessions.newLink
  }

  def `launch anonymous session`(
      projectPage:          ProjectPage,
      projectDetails:       ProjectDetails
  )(implicit anonEnvConfig: AnonEnvConfig): Option[JupyterLabPage] = {
    go to projectPage
    When("user is logged out")
    And(s"goes to ${projectDetails.title}")
    And("clicks on the sessions tab to launch an anonymous notebook")
    click on projectPage.Sessions.tab
    if (anonEnvConfig.isAvailable) {
      And("anonymous sessions are supported")
      Some(`anonymous session supported`(projectPage))
    } else {
      And("anonymous sessions are not supported")
      `anonymous session unsupported`(projectPage)
      None
    }
  }

  def `launch unprivileged session`(implicit anonEnvConfig: AnonEnvConfig): Option[ProjectPage] =
    if (!`project exists in GitLab`(anonEnvConfig.projectId)) {
      logger.warn(s"Project ${anonEnvConfig.projectId} does not exist in the deployment. Skipping this spec")
      None
    } else {
      implicit val projectPage: ProjectPage = ProjectPage(anonEnvConfig.projectId)

      When(s"user goes to ${anonEnvConfig.projectId}")
      go to projectPage

      And("clicks on the Sessions tab to launch an unprivileged session")
      click on projectPage.Sessions.tab

      // We sleep a little to complete anonymous "login"
      sleep(5 seconds)
      `click new & wait for image to build`
      `start session and wait until it is ready`

      val jupyterLabPage = `try few times before giving up` { _ =>
        projectPage.Sessions.Running.connectToJupyterLab
      }

      Then("a JupyterLab page is opened on a new tab")
      verify browserSwitchedTo jupyterLabPage sleep (5 seconds)

      Some(projectPage)
    }

  private def `anonymous session unsupported`(projectPage: ProjectPage): Unit = {
    Then("they should see a message")
    projectPage.Sessions.anonymousSessionsUnsupportedInfo.isDisplayed shouldBe true
  }

  private def `anonymous session supported`(implicit projectPage: ProjectPage): JupyterLabPage = {
    sleep(5 seconds)
    `click new & wait for image to build`
    `start session and wait until it is ready`

    val jupyterLabPage = projectPage.Sessions.Running.connectToJupyterLab

    Then("a JupyterLab page is opened on a new tab")
    verify browserSwitchedTo jupyterLabPage sleep (5 seconds)

    jupyterLabPage
  }

  private def `click new & wait for image to build`(implicit projectPage: ProjectPage): Unit = {
    And("then they click on the New link")
    click on projectPage.Sessions.newLink sleep (2 seconds)
    And("once the image is built")
    projectPage.Sessions.verifyImageReady sleep (2 seconds)
  }

  private def `start session and wait until it is ready`(implicit projectPage: ProjectPage): Unit =
    `try few times before giving up` { _ =>
      projectPage.Sessions.verifyImageReady sleep (2 seconds)
      try `try to click the session button`
      catch {
        case _: ElementClickInterceptedException => `try to click the session button`
      }
    }

  private def `try to click the session button`(implicit projectPage: ProjectPage): Unit =
    projectPage.Sessions.maybeStartSessionButton match {
      case None =>
        projectPage.Sessions.connectToJupyterLabLink.isDisplayed shouldBe true
      case Some(startEnvButton) =>
        And("the user clicks on the Start session button")
        click on startEnvButton sleep (5 seconds)

        `try few times before giving up` { _ =>
          Then("they should be redirected to the Sessions -> Running tab")
          verify userCanSee projectPage.Sessions.Running.title
        }

        `try few times before giving up` { _ =>
          When("the session is ready")
          projectPage.Sessions.Running.verifySessionReady
        }

        And("the user clicks on the Connect button in the table")
        // Sleep a little while after clicking to give the server a chance to come up
        click on projectPage.Sessions.Running.title sleep (30 seconds)
    }
}
