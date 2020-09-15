package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, AnonEnv}
import ch.renku.acceptancetests.workflows._
import ch.renku.acceptancetests.pages.ProjectPage

import scala.language.postfixOps

class MinimalFunctionalitySpec
    extends AcceptanceSpec
    with AnonEnv
    with Collaboration
    with Environments
    with Login
    with NewProject
    with RemoveProject
    with Settings
    with Fork {

  scenario("User can use basic functionality of Renku") {

    implicit val loginType: LoginType = logIntoRenku

    implicit val projectDetails: ProjectDetails = ProjectDetails.generate

    createNewProject

    verifyMergeRequestsIsEmpty
    verifyIssuesIsEmpty
    createNewIssue

    setProjectTags
    setProjectDescription

    forkTestCase

    go to ProjectPage()
    removeProjectInGitLab
    verifyProjectWasRemoved

    launchUnprivilegedEnvironment
    stopEnvironment(anonEnvConfig.projectId)

    logOutOfRenku

    launchAnonymousEnvironment map (_ => stopEnvironment(anonEnvConfig.projectId))

  }
}
