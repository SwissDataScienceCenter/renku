package ch.renku.acceptancetests

import ch.renku.acceptancetests.model.projects.ProjectUrl
import ch.renku.acceptancetests.tooling.AcceptanceSpecData
import ch.renku.acceptancetests.workflows.FlightsTutorial
import org.scalatest.FeatureSpec

class CliSpec extends FeatureSpec with AcceptanceSpecData with FlightsTutorial {

  scenario("Tests should be able to test Renku CLI") {

    followTheFlightsTutorial(ProjectUrl("https://dev.renku.ch/gitlab/jakub.chrobasik1/cli-testing.git"))
//    followFlightsTutorial(ProjectUrl("https://renkulab.io/gitlab/jakub.chrobasik/kuba-testing.git"))
  }
}
