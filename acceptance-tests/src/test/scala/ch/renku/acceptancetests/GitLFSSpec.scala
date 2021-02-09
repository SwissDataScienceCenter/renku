package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import ch.renku.acceptancetests.workflows._

class GitLFSSpec  extends AcceptanceSpec
  with Collaboration
  with Environments
  with Login
  with NewProject{

  scenario("User can start a notebook with auto-fetch LFS") {

    implicit val loginType: LoginType = logIntoRenku
    implicit val projectDetails: ProjectDetails = ProjectDetails.generate()

    createNewProject(projectDetails)

  }

  }
