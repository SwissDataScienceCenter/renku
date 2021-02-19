package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects._
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, BatchRemoveProjectSpecData}
import ch.renku.acceptancetests.workflows._

import scala.concurrent.duration._
import scala.language.postfixOps
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
        if (config.batchRemove) `login and remove projects`(config)
        else {
          Given("specifically asked to not remove projects")
          Then("do not remove anything")
        }
      case None =>
        Given("not asked to remove projects")
        Then("do not remove anything")
    }
  }

  def `login and remove projects`(config: BatchRemoveConfig): Unit = {
    implicit val loginType: LoginType = `log in to Renku`

    Given("projects to remove")
    removeProjects(config)

    `log out of Renku`
  }

  private def removeProjects(config: BatchRemoveConfig): Unit = {
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
      ProjectIdentifier.unsafeApply(namespace, slug)
    })
    removeIds foreach removeProject
    go to ProjectsPage sleep (5 seconds)
  }

  private def removeProject(projectId: ProjectIdentifier): Unit = {
    // Go to the project page to get the title
    val projectPage = ProjectPage(projectId)
    go to projectPage sleep (5 seconds)
    projectPage.projectTitle match {
      case Some(s) =>
        And(s"found project $s to remove")
        Then("remove project")
        `remove project in GitLab`(projectId)
      case None =>
        And(s"could not get the title for $projectId")
        Then("do not remove")
    }
  }
}
