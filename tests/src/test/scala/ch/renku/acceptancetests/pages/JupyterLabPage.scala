package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.{ProjectDetails, ProjectIdentifier}
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import org.openqa.selenium.Keys.RETURN
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

object JupyterLabPage {
  def apply()(implicit projectDetails: ProjectDetails, userCredentials: UserCredentials): JupyterLabPage =
    new JupyterLabPage(projectDetails.title.toPathSegment, userCredentials.userNamespace)

  def apply(projectId: ProjectIdentifier): JupyterLabPage =
    new JupyterLabPage(projectId.slug, projectId.namespace)
}

class JupyterLabPage(projectSlug: String, namespace: String) extends RenkuPage {

  override val title: Title = "JupyterLab"
  override val path: Path = Refined.unsafeApply(
    s"/jupyterhub/user/${namespace}/${projectSlug}"
  )

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(terminalIcon)

  def terminalIcon(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("div.jp-LauncherCard [data-icon='ui-components:terminal']")) getOrElse fail("Terminal icon not found")
  }

  object terminal {

    def %>(command: String)(implicit webDriver: WebDriver): Unit = eventually {
      val terminalElement = find(cssSelector("#jp-Terminal-0 > div > div.xterm-screen > canvas.xterm-cursor-layer")) getOrElse fail(
        "Terminal not found"
      )

      new Actions(webDriver)
        .moveToElement(terminalElement)
        .click()
        .sendKeys(s"$command$RETURN")
        .build()
        .perform()
    }
  }
}
