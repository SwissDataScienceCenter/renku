package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

case object ProjectsPage extends RenkuPage with TopBar {
  override val path:  Path  = "/projects"
  override val title: Title = "Renku"

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(YourProjects.tab)

  object YourProjects {

    def tab(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("li.nav-item a[href='/projects']")) getOrElse fail("Projects -> Your Projects tab not found")
    }

    def linkTo(
        project:          ProjectDetails
    )(implicit webDriver: WebDriver, userCredentials: UserCredentials): WebElement =
      maybeLinkTo(project) getOrElse fail(s"Projects -> Your Projects -> '${project.title}' link not found")

    def maybeLinkTo(
        project:          ProjectDetails
    )(implicit webDriver: WebDriver, userCredentials: UserCredentials): Option[WebElement] = eventually {
      find(cssSelector(s"a[href='/projects/${userCredentials.username}/${project.title.toPathSegment}']"))
    }
  }
}
