/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
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

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeDriverService, ChromeOptions}
import org.scalatest.{BeforeAndAfterAll, Suite}

trait WebDriveredSpec extends BeforeAndAfterAll {
  self: Suite =>

  implicit lazy val webDriver: WebDriver = {
    System.setProperty("webdriver.http.factory", "jdk-http-client")
    startWebDriver
  }

  private def startWebDriver: WebDriver =
    sys.env.get("DOCKER") match {
      case Some(_) =>
        new ChromeDriver(
          new ChromeDriverService.Builder().withAllowedListIps("127.0.0.1").build,
          new ChromeOptions().addArguments("no-sandbox", "headless", "disable-gpu", "window-size=1920,1600")
        )
      case None =>
        new ChromeDriver(
          new ChromeOptions().addArguments("window-size=1920,1600")
        )
    }

  protected override def afterAll(): Unit = {
    webDriver.quit()
    super.afterAll()
  }
}
