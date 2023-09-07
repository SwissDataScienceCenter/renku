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

package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.GitLabBaseUrl
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.projects.{ProjectDetails, ProjectIdentifier}
import ch.renku.acceptancetests.model.users.UserCredentials
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalactic.source
import org.scalatest.enablers.Retrying
import org.scalatestplus.selenium.WebBrowser
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}

import scala.concurrent.duration._

object ProjectPage {

  def createFrom(projectDetails: ProjectDetails)(implicit userCredentials: UserCredentials): ProjectPage =
    new ProjectPage(projectDetails.title.toPathSegment, userCredentials.userNamespace)

  def apply(projectId: ProjectIdentifier): ProjectPage =
    new ProjectPage(projectId.path, projectId.namespace)
}

class ProjectPage(val projectSlug: String, val namespace: String)
    extends RenkuPage(
      path = s"/projects/$namespace/$projectSlug",
      title = raw"(.*)(• Project •)(.*)"
    )
    with TopBar {

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(Overview.tab)

  def viewInGitLab(implicit gitLabBaseUrl: GitLabBaseUrl, webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(s"a[href='$gitLabBaseUrl/$namespace/$projectSlug']"))
      .getOrElse(fail("View in GitLab button not found"))
  }

  def projectTitle(implicit webDriver: WebDriver): Option[String] = {
    val fullTitle =
      find(cssSelector("div > main > div:nth-child(2) > div > div > div.row > div.col-12.col-md > h3")) map (_.text)
    val visibility = find(
      cssSelector("div > main > div:nth-child(2) > div > div > div.row > div.col-12.col-md > h3 > span")
    ) map (_.text)
    (fullTitle, visibility) match {
      case (Some(t), Some(v)) =>
        Some(t.substring(0, t.length - v.length) trim)
      case _ => None
    }
  }

  def startButtonVisible(implicit webDriver: WebDriver): Boolean = eventually {
    find(cssSelector("a.start-session-button > svg.fa-play")).fold(false)(_.isEnabled)
  }

  def connectButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("button.start-session-button")) getOrElse fail("'Connect' button not found")
  }

  def connectButtonVisible(implicit webDriver: WebDriver): Boolean = eventually {
    find(cssSelector("button.start-session-button > div > img[src='/connect.svg']")).fold(false)(_.isEnabled)
  }

  object Overview {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path']")) getOrElse fail("Overview tab not found")
    }

    def projectDescription(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("[data-cy='entity-description']")) getOrElse fail("Overview -> Project Description not found")
    }

    def overviewGeneralButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"#nav-overview-general"))
        .getOrElse(fail("Overview -> General button not found"))
    }

    def cloneButton(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector(s"button.btn.btn-outline-rk-green"))
        .find(button => button.text == "Clone")
        .getOrElse(fail("Overview -> Clone button not found"))
    }

    def projectHttpUrl(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector(s".dropdown-menu code"))
        .find(e => e.text.matches("^http.+.git$")) getOrElse fail("Project http url not found")
    }

    object Description {

      def title(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.row div.card-header")) getOrElse fail("Overview -> Description title")
      }
    }
  }

  object Files {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/files']")) getOrElse fail("Files tab not found")
    }

    object FileView {

      def lineageTab(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector(s"button[type=button]")).find(_.text == "Lineage") getOrElse fail(s"Lineage tab not found")
      }

      def file(fileName: String)(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"a[href='$path/files/blob/$fileName']")) getOrElse fail(s"$fileName not found")
      }

      def folder(folderName: String)(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector(s"div.fs-element")).find(_.text == folderName) getOrElse fail(s"$folderName not found")
      }

      def lineage(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"div.graphContainer > svg")) getOrElse fail(
          s"Lineage SVG not found"
        )
      }
    }

    object Info {

      def heading(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("#file-card-header")) getOrElse fail(
          "Files -> Info heading not found"
        )
      }

      def commit(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("ul.list-group.list-group-flush li div div a")) getOrElse fail(
          "Files -> Info commit not found"
        )
      }

      def creatorAndTime(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("ul.list-group.list-group-flush li div div.caption")) getOrElse fail(
          "Files -> Info creator and time not found"
        )
      }

      def title(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.card-body h1")) getOrElse fail("Files -> Info title not found")
      }

      def content(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector("div.card-body h2 + p"))
          .find(_.text.startsWith("This is a Renku project"))
          .getOrElse(fail("Files -> Info content not found"))
      }
    }

    object Notebook {
      def notebookRender(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.card-body > div.notebook-render")) getOrElse fail(
          "File -> Notebook render element not found"
        )
      }

      def cells(implicit webDriver: WebDriver): Iterator[WebBrowser.Element] = eventually {
        findAll(cssSelector("div.card-body > div.ipynb-renderer-root div.cell div"))
      }

      def cellWithText(text: String)(implicit webDriver: WebDriver): WebElement = eventually {
        cells.find(_.text == text) getOrElse fail("No result cell found")
      }
    }
  }

  object Datasets {
    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/datasets']")) getOrElse fail("Datasets tab not found")
    }

    def title(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("h2"))
        .find(_.text.startsWith("Datasets"))
        .getOrElse(fail("Datasets title not found"))
    }

    def addDatasetButton(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("a"))
        .find(element => element.text.contains("Add Dataset") || element.text.contains("Add a Dataset"))
        .getOrElse(fail("Add a Dataset button not found"))
    }

    def goBackButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("[data-cy='go-back-button']"))
        .getOrElse(fail("Dataset go back button not found"))
    }

    object DatasetsList {
      def maybeLink(to: DatasetPage)(implicit webDriver: WebDriver): Option[WebElement] = eventually {
        find(cssSelector(s"a[href='${to.path}/'] .card-title"))
      }
    }
  }

  object Sessions {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/sessions']")) getOrElse fail("Sessions tab not found")
    }

    def sessionStartMore(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"button[data-cy='more-menu']")) getOrElse fail("Sessions more options not found")
    }

    def sessionStartWithOptions(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"button[role='menuitem'] > a.text-decoration-none")) getOrElse
        fail("Sessions start with options not found")
    }

    def newLink(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector(s"div.d-flex > div > a[href='$path/sessions/new']"))
        .find(_.text == "New session")
        .getOrElse(fail("New session link not found"))
    }(waitUpTo(1 minute), implicitly[Retrying[WebBrowser.Element]], implicitly[source.Position])

    def anonymousSessionsUnsupportedInfo(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("div > div > div > div > p"))
        .find(
          _.text == "This Renkulab deployment does not allow unauthenticated users to start sessions."
        )
        .getOrElse(fail("Unsupported session notification not found"))
    }

    def startSessionButton(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("button.btn"))
        .find(_.text == "Start session")
        .getOrElse(fail("'Start session' button not found"))
    }

    def connectToJupyterLabLink(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("a[href*='/sessions/']"))
        .find(_.text.endsWith("Open in new tab"))
        .getOrElse(fail("Connect to session button not found"))
    }

    def imageReadyBadge(implicit webDriver: WebDriver): WebElement =
      find(cssSelector(".badge.bg-success"))
        .getOrElse(fail("Image ready badge cannot be found"))

    object Running {

      def title(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(".sessions-title"))
          .map { element =>
            element.text shouldBe "Sessions"
            element
          }
          .getOrElse(fail("Sessions -> Running: title not found"))
      }

      def maybeOpenButton(implicit webDriver: WebDriver): Option[WebElement] = eventually {
        find(cssSelector("a[href*='sessions/show']"))
          .find(_.text.trim == "Connect")
      }

      def maybeGetLogsButton(implicit webDriver: WebDriver): Option[WebElement] = eventually {
        find(cssSelector("button.text-nowrap.btn.btn-secondary"))
          .find(_.text.trim == "Get logs")
      }

      def openButtonMenu(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.sessionsButton > button"))
          .getOrElse(fail("Menu on the 'Open' button not found"))
      }

      def openInNewTabMenuItem(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("a.dropdown-item[href*='/sessions/']"))
          .getOrElse(fail("'Open in new Tab' menu option not found"))
      }

      def sessionDropdownMenu(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector("div.sessionsButton > button"))
          .find(_.text.isEmpty())
          .getOrElse(fail("Session dropdown button not found"))
      }

      def stopSessionModal(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("#stop-session-button"))
          .getOrElse(fail("Session stop button not found in the navbar"))
      }

      def stopButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("[data-cy='stop-session-modal-button']"))
          .getOrElse(fail("Session stop button not found in the modal"))
      }

      def goBackButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("[data-cy='go-back-button']"))
          .getOrElse(fail("Session go back button not found"))
      }

      def sessionReadyBadge(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(".text-nowrap.p-1.badge.bg-success"))
          .getOrElse(fail("Session ready badge cannot be found"))
      }

      def maybeSessionNotReadyBadge(implicit webDriver: WebDriver): Option[WebElement] = eventually {
        find(cssSelector(".text-nowrap.p-1.badge.bg-warning"))
      }

      def maybeJupyterLabIframe(implicit webDriver: WebDriver): Option[WebElement] = eventually {
        find(cssSelector("#session-iframe"))
      }

      def goBackButtonFullscreen(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(".fullscreen-back-button"))
          .getOrElse(fail("Fullscreen Session go back button not found"))
      }
    }
  }

  object Settings {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/settings']")) getOrElse fail("Settings tab not found")
    }

    object General {

      // Status Panel
      def projectUpToDate(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("[data-cy='project-version-section-open']")) getOrElse fail("Settings -> Project up to date")
      }

      def projectRenkuVersion(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("a[href*='https://github.com/SwissDataScienceCenter/renku-python/releases/tag/']"))
          .getOrElse(fail("Settings -> Renku Version not found"))
      }

      def projectRenkuVersionOk(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("[data-cy='project-settings-migration-status'] svg.fa-check-circle"))
          .getOrElse(fail("Settings -> Renku Version not up to date"))
      }

      def updateVersionButton(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector("button.btn")).toList
          .find(_.text == "Update version")
          .getOrElse(fail("'Update version' button not found"))
      }
      // /Status Panel

      def addProjectTags(tags: String)(implicit webDriver: WebDriver): Unit = eventually {
        projectTags enterValue tags
        updateButton.click() sleep (5 seconds)
      }

      def projectTags(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("input#projectTags")) getOrElse fail("Project Tags field not found")
      }

      def updateProjectDescription(description: String)(implicit webDriver: WebDriver): Unit = eventually {
        projectDescription enterValue description
        updateButton.click() sleep (5 seconds)
      }

      def projectDescription(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("input#projectDescription")) getOrElse fail("Project Description field not found")
      }

      def updateButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(".updateProjectSettings")) getOrElse fail(
          "Update button not found"
        )
      }
    }
  }

  def forkButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#fork-project")) getOrElse fail("Fork button not found")
  }

  object ForkDialog {

    def submitFormWith(project: ProjectDetails)(implicit webDriver: WebDriver): Unit = eventually {
      val tf = titleField
      // Clear does not work here, just send backspace a bunch of times
      tf.clear() sleep (1 second)
      tf enterValue List.fill(40)("\b").mkString("")
      tf enterValue project.title

      forkButton.click() sleep (10 second)
    }

    private def titleField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#title")) getOrElse fail("Title field not found")
    }

    def forkButton(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("div.modal-footer > button.btn"))
        .find(_.text.trim == "Fork Project") getOrElse fail("Fork button not found")
    }
  }

  lazy val asProjectIdentifier: ProjectIdentifier = ProjectIdentifier(namespace, projectSlug)
}
