/*
 * Copyright 2021 Swiss Data Science Center (SDSC)
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

import java.lang.System.getProperty

import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.{Driver, WebBrowser}

/**
  * Helper class for capturing screenshots for documentation.
  *
  * @param frame Object that can capture a screen
  * @param captureScreenshots True if should screenshot at checkpoint
  */
class DocsScreenshots(test: AcceptanceSpec, browser: WebBrowser with Driver) {

  lazy val captureScreenshots: Boolean = {
    Option(getProperty("docsrun")) orElse sys.env.get("RENKU_TEST_DOCS_RUN") match {
      case Some(s) => s.nonEmpty
      case None    => false
    }
  }

  /**
    * Informs helper to make a screenshot if we are capturing them.
    */
  def reachedCheckpoint(): Unit = {
    implicit val b:         WebBrowser with Driver = browser
    implicit val webDriver: WebDriver              = test.webDriver
    if (captureScreenshots)
      test.saveScreenshot
  }

}
