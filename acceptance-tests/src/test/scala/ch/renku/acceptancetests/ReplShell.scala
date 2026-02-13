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

import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.Chrome

/** Import packages necessary for working with selenium in the console.
  *
  * To get started,
  * - run `sbt test:console`
  * - :load src/test/scala/ch/renku/acceptancetests/ReplShell.scala
  *
  * Then you can execute statements like
  * ```
  * val webDriver = ReplShell.getChromeWebDriver("https://dev.renku.ch")
  * val loginButtons = webDriver.findElements(By.cssSelector("a[href='/login']"))
  * ```
  */
object ReplShell {

  def getChromeWebDriver(url: String): WebDriver = {
    val driver = Chrome.webDriver
    driver.get(url)
    driver
  }

}
