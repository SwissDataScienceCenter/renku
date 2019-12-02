package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.ProjectDetails
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
    new JupyterLabPage(projectDetails, userCredentials)
}

class JupyterLabPage(projectDetails: ProjectDetails, userCredentials: UserCredentials) extends RenkuPage {

  override val title: Title = "JupyterLab"
  override val path: Path = Refined.unsafeApply(
    s"/jupyterhub/user/${userCredentials.userNamespace}/${projectDetails.title.toPathSegment}"
  )

  def terminalIcon(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("div[data-icon=terminal]")) getOrElse fail("Terminal icon not found")
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
