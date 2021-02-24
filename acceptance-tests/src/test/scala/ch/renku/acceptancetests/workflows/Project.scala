package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects.ProjectDetails
import ch.renku.acceptancetests.tooling.AcceptanceSpec

trait Project {
  self: AcceptanceSpec =>

  protected var projectDetails: ProjectDetails
}
