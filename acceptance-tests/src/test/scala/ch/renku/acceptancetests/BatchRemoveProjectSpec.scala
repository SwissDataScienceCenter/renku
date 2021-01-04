package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.{ProjectDetails, ProjectIdentifier, Template, Visibility}
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, BatchRemoveProjectSpecData}
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.pages.GitLabPages.GitLabBaseUrl
import org.scalatestplus.selenium.WebBrowser
import eu.timepit.refined.api.Refined

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.util.matching.Regex

/**
  * Delete many projects.
  *
  * During testing, test projects can accumulate and they are tedious to clean up. This spec
  * will delete all projects that match a pattern and can be used to batch delete projects
  * from failed test runs, for example.
  *
  * It needs to be explicitly activated to avoid accidentally destroying projects.
  */
class BatchRemoveProjectSpec extends AcceptanceSpec with BatchRemoveProjectSpecData with Login with RemoveProject {

  scenario("User can delete many projects project") {
    batchRemoveConfig match {
      case Some(config) =>
        if (config.batchRemove) {
          loginAndRemoveProjects(config)
        } else {
          Given("specifically asked to not remove projects")
          Then("do not remove anything")
        }
      case None =>
        Given("not asked to remove projects")
        Then("do not remove anything")
    }
  }

  def loginAndRemoveProjects(config: BatchRemoveConfig): Unit = {
    implicit val loginType: LoginType = logIntoRenku
    Given("projects to remove")
    removeProjects(config, loginType)
    logOutOfRenku
  }

  def removeProjects(config: BatchRemoveConfig, loginType: LoginType)(implicit gitLabBaseUrl: GitLabBaseUrl): Unit = {
    When("user goes to the projects page")
    go to ProjectsPage sleep (5 seconds)
    verify browserAt ProjectsPage
    // Wait for the page to load
    val pattern: Regex = config.pattern.r
    val projectLinks = ProjectsPage.YourProjects.projectLinks
    And("lists projects to remove")
    val toRemoveLinks = projectLinks.filter { elt =>
      val href = elt getAttribute "href"
      val last = (href split "/") last;
      pattern matches last
    }
    val removeIds = toRemoveLinks map (elt => {
      val projectUrlComps = elt getAttribute "href" split "/"
      val namespace       = projectUrlComps(projectUrlComps.size - 2)
      val slug            = projectUrlComps last;
      ProjectIdentifier(Refined.unsafeApply(namespace), Refined.unsafeApply(slug))
    })
    removeIds foreach (removeProject(_, loginType))
    go to ProjectsPage sleep (5 seconds)
  }

  def removeProject(projectId: ProjectIdentifier, loginType: LoginType)(implicit gitLabBaseUrl: GitLabBaseUrl): Unit = {
    // Go to the project page to get the title
    val projectPage = ProjectPage(projectId)
    go to projectPage sleep (5 seconds)
    val title = projectPage.projectTitle
    title match {
      case Some(s) =>
        implicit val projectDetails: ProjectDetails =
          ProjectDetails(Refined.unsafeApply(s),
                         Visibility.Public,
                         Refined.unsafeApply(""),
                         Template(Refined.unsafeApply("Unused")),
                         ""
          )
        And(s"found project $s to remove")
        removeProjectInGitLab
        Then("remove project")
      case None =>
        And(s"could not get the title for $projectId")
        Then("do not remove")
    }
  }

}
