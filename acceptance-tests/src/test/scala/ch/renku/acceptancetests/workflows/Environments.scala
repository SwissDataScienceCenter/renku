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

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnvConfig}

import scala.concurrent.duration._

trait Environments {
  self: AcceptanceSpec =>

  def `launch a session`(implicit projectPage: ProjectPage): JupyterLabPage = {

    When("user clicks on the start sessions")
    click on projectPage.Sessions.sessionStartMore
    docsScreenshots.takeScreenshot()
    click on projectPage.Sessions.sessionStartWithOptions
    docsScreenshots.takeScreenshot()

    `wait for image to build`
    docsScreenshots.takeScreenshot()

    `start session and wait until it is ready`
    docsScreenshots.takeScreenshot()

    `connect to JupyterLab`
  }

  private def `connect to JupyterLab`(implicit projectPage: ProjectPage): JupyterLabPage = eventually {
    And("the user tries to start a JupyterLab session")
    sleep(2 seconds)

    if (projectPage.Sessions.Running.maybeOpenButton.fold(ifEmpty = true)(btn => !btn.isDisplayed))
      pause whenUserCanSee { driver =>
        val maybeGetLogs = projectPage.Sessions.Running.maybeOpenButton(driver)
        if (maybeGetLogs.fold(false)(_.isDisplayed)) And("the 'Get logs' button is displayed")
        maybeGetLogs
      }

    `try few times before giving up` { _ =>
      And("clicks on the 'Connect' button")
      click on projectPage.Sessions.Running.maybeOpenButton.getOrElse(fail("Connect button not found"))
      sleep(15 seconds)
    }

    And("switches to the iframe")
    `try few times before giving up` { _ =>
      projectPage.Sessions.Running.maybeJupyterLabIframe match {
        case Some(iframe) =>
          And("finds iframe")
          webDriver.switchTo().frame(iframe)
          sleep(5 seconds)

          Then("a JupyterLab session is opened")
          JupyterLabPage(projectPage)
        case None =>
          And("does not find iframe")
          sleep(5 seconds)
          fail("Cannot find JupyterLab iframe")
      }
    }
  }

  def `stop session`(implicit projectPage: ProjectPage): Unit =
    stopSessionOnProjectPage(projectPage)

  private def stopSessionOnProjectPage(projectPage: ProjectPage): Unit = {
    When("the user switches back to the Renku frame")
    webDriver.switchTo().defaultContent()
    // And("opens the sessions menu")
    // click on projectPage.Sessions.Running.sessionDropdownMenu sleep (1 second)
    And("clicks on stop button to open modal")
    click on projectPage.Sessions.Running.stopSessionModal sleep (5 seconds)
    And("clicks on stop button")
    click on projectPage.Sessions.Running.stopButton sleep (10 seconds)
    // Then("the session gets stopped and they can see the New Session link")
    // verify userCanSee projectPage.Sessions.newLink
    When("the user switches back to the Renku project")
    click on projectPage.Sessions.Running.goBackButton sleep (10 seconds)
  }

  def `launch anonymous session`(
      projectPage:          ProjectPage,
      projectDetails:       ProjectDetails
  )(implicit anonEnvConfig: AnonEnvConfig): Option[JupyterLabPage] = {
    go to projectPage sleep (5 seconds)
    verify browserAt projectPage
    When("user is logged out")
    And(s"goes to ${projectDetails.title}")
    And("clicks on the sessions tab to launch an anonymous notebook")
    click on projectPage.Sessions.sessionStartMore
    click on projectPage.Sessions.sessionStartWithOptions
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
      click on projectPage.Sessions.sessionStartMore
      click on projectPage.Sessions.sessionStartWithOptions
      sleep(5 seconds)

      if (projectPage.Sessions.Running.maybeOpenButton.exists(_.isDisplayed))
        And("the session is already started")
      else {
        `wait for image to build`
        `start session and wait until it is ready`
      }

      `connect to JupyterLab`
      Then("a JupyterLab session is opened")
      Some(projectPage)
    }

  private def `anonymous session unsupported`(projectPage: ProjectPage): Unit = {
    Then("they should see a message")
    projectPage.Sessions.anonymousSessionsUnsupportedInfo.isDisplayed shouldBe true
  }

  private def `anonymous session supported`(implicit projectPage: ProjectPage): JupyterLabPage = {
    sleep(5 seconds)
    `wait for image to build`
    `start session and wait until it is ready`
    val jupyterLabPage = `connect to JupyterLab`
    Then("a JupyterLab session is opened")
    jupyterLabPage
  }

  private def `wait for image to build`(implicit projectPage: ProjectPage): Unit =
    reload whenUserCannotSee { _ =>
      `wait if a session is in an uncertain state`

      And("wait for the image to build")
      `try again if failed` { _ =>
        val badge = projectPage.Sessions.imageReadyBadge
        sleep(10 seconds)
        badge
      }
    }

  private def `wait if a session is in an uncertain state`(implicit projectPage: ProjectPage) =
    `try few times before giving up` { _ =>
      if (projectPage.maybeBouncer.isDisplayed) {
        do {
          And("wait for the Bouncer to go")
          sleep(20 seconds)
        } while (projectPage.maybeBouncer.isDisplayed)
      } else if (pageSource contains "A session for this commit is starting or terminating") {
        do {
          And("wait for the Session to start or terminate")
          sleep(20 seconds)
        } while (pageSource contains "A session for this commit is starting or terminating")
      }
    }

  private def `start session and wait until it is ready`(implicit projectPage: ProjectPage): Unit = {
    reload whenUserCannotSee (projectPage.Sessions.startSessionButton(_))

    And("the user clicks on the Start session button")
    // it looks like the first click may simply move the page to the button
    // and only the second attempt does the job
    `try again if failed` { _ =>
      click on projectPage.Sessions.startSessionButton
    }
    sleep(5 seconds)

    `try again if failed` { _ =>
      And("the user clicks on the goBackButton session button")
      click on projectPage.Sessions.Running.goBackButtonFullscreen
    }

    `try few times before giving up` { driver =>
      pause whenUserCanSee { drv =>
        val maybeBadge = projectPage.Sessions.Running.maybeSessionNotReadyBadge(drv)
        if (maybeBadge.fold(ifEmpty = false)(_.isDisplayed)) {
          When("the session is not ready the user waits")
          sleep(5 seconds)
        }
        maybeBadge
      }

      When("the session is ready")
      verify userCanSee projectPage.Sessions.Running.sessionReadyBadge(driver)
    }
  }
}
