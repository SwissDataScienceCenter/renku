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

package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.projects.{ProjectDetails, ProjectIdentifier}
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.scalactic.source
import org.scalatest.enablers.Retrying
import org.scalatestplus.selenium
import org.scalatestplus.selenium.WebBrowser
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}

import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

object ProjectPage {

  def createFrom(projectDetails: ProjectDetails)(implicit userCredentials: UserCredentials): ProjectPage =
    new ProjectPage(projectDetails.title.toPathSegment, userCredentials.userNamespace)

  def apply(projectId: ProjectIdentifier): ProjectPage =
    new ProjectPage(projectId.slug, projectId.namespace)
}

class ProjectPage(val projectSlug: String, val namespace: String)
    extends RenkuPage(
      path = s"/projects/$namespace/$projectSlug",
      title = "Renku"
    )
    with TopBar {

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(Overview.tab)

  def viewInGitLab(implicit webDriver: WebDriver): WebElement = eventually {
    find(
      cssSelector(s"a[href*='/gitlab/$namespace/$projectSlug']")
    ) getOrElse fail("View in GitLab button not found")
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

  object Overview {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path']")) getOrElse fail("Overview tab not found")
    }

    def projectDescription(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(".rk-project-description")) getOrElse fail("Overview -> Project Description not found")
    }

    def overviewGeneralButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"#nav-overview-general"))
        .getOrElse(fail("Overview -> General button not found"))
    }

    object Description {

      def title(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.row div.card-header")) getOrElse fail("Overview -> Description title")
      }
    }
  }

  object Collaboration {
    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/collaboration/issues']")) getOrElse fail("Collaboration tab not found")
    }

    object MergeRequests {

      def tab(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"a[href='$path/collaboration/mergerequests']")) getOrElse fail("Merge Requests tab not found")
      }

      def gitLabMrLink(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"a[href*='/gitlab/$namespace/$projectSlug/-/merge_requests']"))
          .getOrElse(fail("GitLab MR link not found"))
      }

      def collaborationIframe(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("#collaboration-iframe"))
          .getOrElse(fail("MR iFrame not found"))
      }

      def futureMergeRequestBanner(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector("div.alert-warning > p"))
          .find(_.text == " Do you want to create a merge request for branch test-branch?")
          .getOrElse(fail("No future merge requests banner found"))
      }

      def createMergeRequestButton(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector(".btn.btn-success")).find(_.text == "Create Merge Request") getOrElse fail(
          "Create Merge Request button not found"
        )
      }
    }

    object Issues {

      def tab(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"a[href='$path/collaboration/issues']")) getOrElse fail("Issues tab not found")
      }

      def gitLabIssuesLink(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"a[href*='/gitlab/$namespace/$projectSlug/-/issues']"))
          .getOrElse(fail("GitLab issue link not found"))
      }

      def collaborationIframe(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("#collaboration-iframe"))
          .getOrElse(fail("Issues iFrame not found"))
      }

      def unavailableCollaborationIframe(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector("div > div.my-4"))
          .find(_.text.startsWith("This Gitlab instance cannot be embedded"))
          .getOrElse(fail("Message iFrame cannot be embedded - not found"))
      }

      object NewIssue {
        def titleField(implicit webDriver: WebDriver): WebElement = eventually {
          find(cssSelector("input#title")) getOrElse fail("Title field not found")
        }

        def markdownSwitch(implicit webDriver: WebDriver): WebElement = eventually {
          find(cssSelector("#CKEditorSwitch")) getOrElse
            fail("Markdown switch not found")
        }

        def descriptionField(implicit webDriver: WebDriver): WebElement = eventually {
          find(cssSelector("textarea#descriptiontext-area")) getOrElse fail("Description field not found")
        }

        def createIssueButton(implicit webDriver: WebDriver): WebElement = eventually {
          findAll(cssSelector("button[type='submit']")).find(_.text.contains("Create Issue")) getOrElse fail(
            "Create Issue button not found"
          )
        }

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
        findAll(cssSelector("div.card-body > div.notebook-render div div"))
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

    def addADatasetButton(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("a"))
        .find(element => element.text.contains("Add Dataset") || element.text.contains("Add a Dataset"))
        .getOrElse(fail("Add a Dataset button not found"))
    }

    object DatasetsList {
      def link(to: DatasetPage)(implicit webDriver: WebDriver): WebElement =
        eventually {
          find(cssSelector(s"a[href='${to.path}/'] > div > div.title"))
            .getOrElse(fail(s"Dataset '${to.path}' not found"))
        }(waitUpTo(60 seconds), implicitly[Retrying[WebBrowser.Element]], implicitly[source.Position])
    }
  }

  object Sessions {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/sessions']")) getOrElse fail("Sessions tab not found")
    }

    def newLink(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector(s"div.d-flex > div > a[href='$path/sessions/new']"))
        .find(_.text == "New session")
        .getOrElse(fail("New session link not found"))
    }(waitUpTo(1 minute), implicitly[Retrying[WebBrowser.Element]], implicitly[source.Position])

    def anonymousUnsupported(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("div > div > div > div > p"))
        .find(
          _.text == "This Renkulab deployment doesn't allow unauthenticated users to start sessions."
        )
        .getOrElse(fail("Unsupported session notification not found"))
    }

    def maybeStartSessionButton(implicit webDriver: WebDriver): Option[WebElement] = eventually {
      findAll(cssSelector("button.btn.btn-primary")).find(_.text == "Start session")
    }

    def connectToJupyterLabLink(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("a[href*='/sessions/']"))
        .find(_.text.endsWith("Open in new tab"))
        .getOrElse(fail("Connect to session button not found"))
    }

    def buttonShowBranch(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("div.mb-3 > button"))
        .find(_.text.startsWith("Do you want to select the branch"))
        .getOrElse(fail("Expanding selection to branch, commit, image not found"))
    }

    def maybeButtonHideBranch(implicit webDriver: WebDriver): Option[WebElement] = eventually {
      findAll(cssSelector("div.mb-3 > button")).find(_.text.startsWith("Hide branch"))
    }

    private def maybeImageReadyBadge(implicit webDriver: WebDriver): Option[WebBrowser.Element] =
      find(cssSelector(".badge.bg-success"))

    def verifyImageReady(implicit webDriver: WebDriver): Unit = eventually {
      sleep(1 second)
      maybeButtonHideBranch getOrElse {
        buttonShowBranch.click()
        sleep(5 seconds)
      }
      maybeImageReadyBadge getOrElse {
        sleep(10 seconds)
        webDriver.navigate.refresh()
        fail("Image not ready")
      }
    }(waitUpTo(5 minutes, interval = 5 seconds), implicitly[Retrying[WebBrowser.Element]], implicitly[source.Position])

    object Running {

      def title(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.row div.col h3"))
          .map { element =>
            element.text shouldBe "Sessions"
            element
          }
          .getOrElse(fail("Sessions -> Running: title not found"))
      }

      def connectToJupyterLab(implicit webDriver: WebDriver, spec: AcceptanceSpec): Unit =
        connectToJupyterLab(s"a.dropdown-item[href*='/sessions/']", s"div.sessionsButton > button")

      def connectToAnonymousJupyterLab(implicit webDriver: WebDriver, spec: AcceptanceSpec): Unit =
        connectToJupyterLab(s"a.dropdown-item[href*='/sessions/']", s"div.sessionsButton > button")

      def connectButton(buttonSelector: String)(implicit webDriver: WebDriver): WebElement = eventually {
        find(
          cssSelector(buttonSelector)
        ) getOrElse fail(
          "First row session Connect button not found"
        )
      }

      private def connectToJupyterLab(
          buttonSelector:   String,
          buttonDropdown:   String
      )(implicit webDriver: WebDriver, spec: AcceptanceSpec): Unit = eventually {
        import spec.{And, Then}
        And("tries to connect to JupyterLab " + buttonSelector)
        sleep(2 seconds)
        // check the dropdown availability even when provided to prevent occasional failures
        if (!buttonDropdown.isEmpty() && (findAll(cssSelector(buttonDropdown)) toList).size > 0) {
          connectButton(buttonDropdown).click()
          sleep(2 seconds)
        }
        connectButton(buttonSelector).click()
        sleep(2 seconds)

        // Check if we are connected to JupyterLab
        val tabs = webDriver.getWindowHandles.asScala.toArray
        webDriver.switchTo() window tabs(1)
        if (webDriver.getCurrentUrl contains "spawn-pending") {
          And("JupyterLab is not up yet")
          Then("close the window and try again later")
          // The server isn't up yet. Close the window and try again
          webDriver.close()
          webDriver.switchTo() window tabs(0)
          fail("Could not connect to JupyterLab")
        } else {
          webDriver.switchTo() window tabs(0)
        }
      }

      def sessionDropdownMenu(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector("div.sessionsButton > button"))
          .find(_.text.isEmpty())
          .getOrElse(fail("Session dropdown button not found"))
      }

      def stopButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("button.dropdown-item svg[data-icon='stop-circle']"))
          .getOrElse(fail("Session stop button not found in the dropdown"))
          .parent
      }

      def verifySessionReady(implicit webDriver: WebDriver): Unit = eventually {
        find(cssSelector(".text-nowrap.p-1.badge.bg-success"))
          .getOrElse(fail("Session is not ready"))
      }
    }
  }

  object Settings {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/settings']")) getOrElse fail("Settings tab not found")
    }

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

    def projectHttpUrl(implicit webDriver: WebDriver): WebElement = eventually {
      findAll(cssSelector("table.table.table-sm td"))
        .find(e => e.text.matches("^http.+.git$")) getOrElse fail("Project http url not found")
    }
  }

  def forkButton(implicit webDriver: WebDriver): WebElement = eventually {
    findAll(cssSelector(s"button")).find(_.text == "fork") getOrElse fail("Fork button not found")
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
      find(cssSelector("div.modal-footer > button.btn.btn-primary")) getOrElse fail("Fork button not found")
    }
  }

  lazy val asProjectIdentifier: ProjectIdentifier = ProjectIdentifier(namespace, projectSlug)
}
