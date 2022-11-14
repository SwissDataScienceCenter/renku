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

import org.openqa.selenium.WebElement

import java.lang.Thread.sleep

trait WebElementOps {

  implicit class WebElementOps(element: WebElement) {

    def enterValue(value: String): Unit = {
      typeInValue(value)
      sleep(1000)
      if (element.getAttribute("value") != value) {
        element.clear()
        sleep(500)
        typeInValue(value)
        sleep(1000)
      }
    }

    private def typeInValue(value: String): Unit = value foreach { char =>
      element.sendKeys(char.toString)
      sleep(50)
    }
  }

  implicit class OptionalWebElementOps(maybeElement: Option[WebElement]) {

    def isDisplayed: Boolean = maybeElement.exists(_.isDisplayed)
  }
}
