package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import ch.renku.acceptancetests.tooling.BaseUrl
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.string.Url
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

object GitLabPages {

  def apply()(implicit projectDetails: ProjectDetails, userCredentials: UserCredentials): GitLabPages =
    new GitLabPages(projectDetails, userCredentials)

  case class GitLabBaseUrl(value: String Refined Url) extends BaseUrl(value)

  sealed abstract class GitLabPage extends Page[GitLabBaseUrl]
}

class GitLabPages(
    projectDetails:  ProjectDetails,
    userCredentials: UserCredentials
) {

  import GitLabPages._

  case object ProjectPage extends GitLabPage {

    override val path: Path = Refined.unsafeApply(
      s"/gitlab/${userCredentials.userNamespace}/${projectDetails.title.toPathSegment}"
    )

    override val title: Title = Refined.unsafeApply(
      s"${userCredentials.fullName} / ${projectDetails.title} · GitLab"
    )

    override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(settingsLink)

    def settingsLink(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("span.nav-item-name.qa-settings-item")) getOrElse fail("Settings link not found")
    }
  }

  case object GitLabProjectsPage extends GitLabPage {
    override val path:  Path  = "/gitlab/dashboard/projects"
    override val title: Title = "Projects · Dashboard · GitLab"
    override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(newProjectButton)

    private def newProjectButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("a[href='/gitlab/projects/new']")) getOrElse fail("New Project button not found")
    }
  }

  case object SettingsPage extends GitLabPage {

    override val path: Path = Refined.unsafeApply(
      s"/gitlab/${userCredentials.userNamespace}/${projectDetails.title.toPathSegment}/edit"
    )

    override val title: Title = Refined.unsafeApply(
      s"General · Settings · ${userCredentials.fullName} / ${projectDetails.title} · GitLab"
    )

    override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(Advanced.expandButton)

    object Advanced {

      def expandButton(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("section.advanced-settings button.js-settings-toggle")) getOrElse fail(
          "Advanced -> Expand button not found"
        )
      }

      def removeProject(implicit webDriver: WebDriver): WebElement = eventually {
        find(cssSelector("form > input[value='Remove project'")) getOrElse fail(
          "Advanced -> Remove project button not found"
        )
      }

      def confirmRemoval(project: ProjectDetails)(implicit webDriver: WebDriver): Unit = eventually {
        find(cssSelector("input#confirm_name_input"))
          .getOrElse(fail("Advanced -> Project removal name confirmation input not found"))
          .enterValue(project.title.toPathSegment)

        find(cssSelector("input[value='Confirm'"))
          .getOrElse(fail("Advanced -> Project removal Confirm button not found"))
          .click()
      }
    }
  }
}
