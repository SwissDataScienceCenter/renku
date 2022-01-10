/*
 * Copyright 2022 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.tooling

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Template, Visibility}
import org.openqa.selenium.{JavascriptExecutor, WebDriver}

import java.lang.System.getProperty

/** Helper class for capturing screenshots for documentation.
  */
class DocsScreenshots(test: ScreenCapturing, webDriver: WebDriver) {

  val captureScreenshots: Boolean =
    Option(getProperty("docsrun")) orElse sys.env.get("RENKU_TEST_DOCS_RUN") match {
      case Some(s) => s.nonEmpty
      case None    => false
    }

  private var screenshotSuppressed: Boolean = false

  def disable(): Unit = screenshotSuppressed = true
  def enable():  Unit = screenshotSuppressed = false

  /** Informs helper to make a screenshot if we are capturing them.
    */

  def takeScreenshot(): Unit = takeScreenshot(executeBefore = None)

  def takeScreenshot(executeBefore: String): Unit = takeScreenshot(Some(executeBefore))

  def takeScreenshot(executeBefore: Option[String]): Unit = {

    executeBefore.map { script =>
      webDriver.asInstanceOf[JavascriptExecutor].executeScript(script)
    }

    if (captureScreenshots && !screenshotSuppressed) test.saveScreenshot()
  }

  lazy val projectDetails: ProjectDetails = {
    val readmeTitle = "flights tutorial"
    ProjectDetails(
      readmeTitle,
      Visibility.Public,
      "A renku tutorial project.",
      Template("This template isn't used"),
      readmeTitle
    )
  }
}

object DocsScreenshots {

  def apply(test: ScreenCapturing, webDriver: WebDriver): DocsScreenshots =
    new DocsScreenshots(test, webDriver)
}
