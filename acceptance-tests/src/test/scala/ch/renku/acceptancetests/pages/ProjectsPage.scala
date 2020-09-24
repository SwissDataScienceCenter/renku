package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}

import scala.jdk.CollectionConverters._
import scala.language.postfixOps

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

    /**
     * Return all the project links.
     */
    def projectLinks(implicit webDriver: WebDriver): List[WebBrowser.Element] =
      findAll(
        cssSelector(
          "main > div:nth-child(4) > div > div:nth-child(3) > div > div > div.d-flex.flex-fill.flex-column.ml-2.mw-0.flex-sm-row > div.d-flex.flex-column.text-truncate > p:nth-child(1) > b > a"
        )
      ) toList
  }
}
