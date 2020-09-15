package ch.renku.acceptancetests.tooling

import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

import org.scalatest.{Outcome, TestSuite}
import org.scalatestplus.selenium.{Driver, WebBrowser}

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
