package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.Page.{Path, Title}

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._

import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.duration._
import scala.language.postfixOps

import org.scalactic.source

object ProjectPage {
  def apply()(implicit projectDetails: ProjectDetails, userCredentials: UserCredentials): ProjectPage =
    new ProjectPage(projectDetails, userCredentials)
}

class ProjectPage(projectDetails: ProjectDetails, userCredentials: UserCredentials) extends RenkuPage with TopBar {

  override val title: Title = "Renku"
  override val path: Path = Refined.unsafeApply(
    s"/projects/${userCredentials.userNamespace}/${projectDetails.title.toPathSegment}"
  )

  def viewInGitLab(implicit webDriver: WebDriver): WebElement = eventually {
    find(
      cssSelector(s"a[href*='/gitlab/${userCredentials.userNamespace}/${projectDetails.title.toPathSegment}']")
    ) getOrElse fail("View in GitLab button not found")
  }

  object Overview {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path']")) getOrElse fail("Overview tab not found")
    }

    def projectDescription(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("div.row span.lead")) getOrElse fail("Overview -> Project Description not found")
    }

    def descriptionButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"div.row ul.nav.flex-column li.nav-item a[href='$path']:last-of-type"))
        .find(_.text == "Description")
        .getOrElse(fail("Overview -> Description button not found"))
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

      def noMergeRequests(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.row div.col p"))
          .find(_.text == "No merge requests.")
          .getOrElse(fail("No merge requests info not found"))
      }
    }
  }

  object Files {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/files']")) getOrElse fail("Files tab not found")
    }

    object FileView {

      def file(fileName: String)(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"a[href='$path/files/blob/$fileName']")) getOrElse fail(s"$fileName not found")
      }

      def folder(folderName: String)(implicit webDriver: WebDriver): WebElement = eventually {
        findAll(cssSelector(s"div.fs-element")).find(_.text == folderName) getOrElse fail(s"$folderName not found")
      }
    }

    object Info {

      def heading(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.card div.align-items-baseline.card-header")) getOrElse fail(
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
        find(cssSelector("div.card-body h1 + p")) getOrElse fail("Files -> Info content not found")
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

    object DatasetsList {

      def list(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector(s"div.tree-container nav")) getOrElse fail("Datasets list not found")
      }

      def flights(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.tree-container nav a div"))
          .find(_.text == "2019-01 US Flights")
          .getOrElse(fail("Dataset 'flights' not found"))
      }
    }
  }

  object Environments {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/environments']")) getOrElse fail("Environments tab not found")
    }

    def newLink(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/environments/new']")) getOrElse fail("New environment link not found")
    }

    def startEnvironment(implicit webDriver: WebDriver): WebElement = eventually {
      verifyImageReady
      find(cssSelector("button.btn.btn-primary:last-of-type")) getOrElse fail("Start environment button not found")
    }

    def verifyImageReady(implicit webDriver: WebDriver): Unit =
      eventually {
        findImageReadyBadge getOrElse waitForImageToBeReady
      }(
        PatienceConfig(
          timeout  = scaled(Span(300, Seconds)),
          interval = scaled(Span(2, Seconds))
        ),
        implicitly[source.Position]
      )

    private def waitForImageToBeReady(implicit webDriver: WebDriver): Unit = {
      find(cssSelector(".badge.badge-warning")) getOrElse fail("Image building info badges not found");
      Thread.sleep(2000);
      findImageReadyBadge getOrElse fail("Image not yet built")
    }

    private def findImageReadyBadge(implicit webDriver: WebDriver): Option[WebBrowser.Element] =
      find(cssSelector(".badge.badge-success"))

    object Running {

      def title(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("div.row div.col h3"))
          .map(element => { element.text shouldBe "Interactive Environments"; element })
          .getOrElse(fail("Environments -> Running title not found"))
      }

      def connectButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(
          cssSelector(
            s"a[href*='/jupyterhub/user/${userCredentials.userNamespace}/${projectDetails.title.toPathSegment}-']"
          )
        ) getOrElse fail(
          "First row Interactive Environment Connect button not found"
        )
      }

      def connectDotButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("button.btn.btn-primary svg[data-icon='ellipsis-v']"))
          .getOrElse(fail("First row Interactive Environment ... button not found"))
          .parent
      }

      def stopButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("button.dropdown-item svg[data-icon='stop-circle']"))
          .getOrElse(fail("First row Interactive Environment Stop button not found"))
          .parent
      }

      def verifyEnvironmentReady(implicit webDriver: WebDriver): Unit = eventually {
        find(cssSelector(".text-nowrap.p-1.badge.badge-success")) getOrElse waitForImageToBeReady
      }
    }
  }

  object Settings {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"a[href='$path/settings']")) getOrElse fail("Settings tab not found")
    }

    def addProjectTags(tags: String)(implicit webDriver: WebDriver): Unit = eventually {
      (projectTags sendKeys tags) sleep (2 seconds)
      updateButton.click() sleep (2 seconds)
    }

    def projectTags(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#projectTags")) getOrElse fail("Project Tags field not found")
    }

    def updateProjectDescription(description: String)(implicit webDriver: WebDriver): Unit = eventually {
      (projectDescription sendKeys description) sleep (2 seconds)
      updateButton.click() sleep (2 seconds)
    }

    def projectDescription(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#projectDescription")) getOrElse fail("Project Description field not found")
    }

    def updateButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("div.form-group + button.btn.btn-primary")) getOrElse fail(
        "Update button not found"
      )
    }
  }

  def forkButton(implicit webDriver: WebDriver): WebElement = eventually {
    findAll(cssSelector(s"button")).find(_.text == "fork") getOrElse fail("Fork button not found")
  }

  object ForkDialog {

    def submitFormWith(project: ProjectDetails)(implicit webDriver: WebDriver): Unit =
      eventually {
        val tf = titleField
        // Clear does not work here, just send backspace a bunch of times
        // tf.clear() sleep (1 second)
        tf.sendKeys(List.fill(40)("\b").mkString(""))
        tf.sendKeys(project.title.value) sleep (1 second)

        forkButton.click() sleep (1 second)
      }

    private def titleField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input#title")) getOrElse fail("Title field not found")
    }

    def forkButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("div.modal-footer > button.btn.btn-primary")) getOrElse fail("Fork button not found")
    }
  }
}
