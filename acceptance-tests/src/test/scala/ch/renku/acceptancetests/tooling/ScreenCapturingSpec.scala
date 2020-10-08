package ch.renku.acceptancetests.tooling

import org.scalatest.Outcome

trait ScreenCapturingSpec extends ScreenCapturing {
  this: AcceptanceSpec =>

  override def withFixture(test: NoArgTest): Outcome = {
    val outcome = test()

    if (outcome.isExceptional) {
      saveScreenshot
    }
    outcome
  }
}
