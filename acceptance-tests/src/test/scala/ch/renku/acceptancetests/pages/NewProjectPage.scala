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

package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Template, Visibility}
import ch.renku.acceptancetests.tooling.DocsScreenshots
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}

import scala.concurrent.duration._

case object NewProjectPage
    extends RenkuPage(
      path = "/projects/new",
      title = "Renku"
    )
    with TopBar {

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(createButton)

  def submitFormWith(
      project:          ProjectDetails
  )(implicit webDriver: WebDriver, docsScreenshots: DocsScreenshots): Unit = eventually {
    titleField.clear() sleep (5 seconds)
    titleField.enterValue(project.title)

    descriptionField.clear() sleep (1 second)
    descriptionField.enterValue(project.description)

    visibilityRadioInput(project.visibility).click() sleep (1 second)

    scrollDown

    templateCard(project.template).click() sleep (5 seconds)

    docsScreenshots.takeScreenshot()

    scrollDown
    // Move the mouse off the field to prevent the tooltip from blocking the button
    new Actions(webDriver)
      .moveByOffset(20, 20)
      .build()
      .perform();

    createButton.click() sleep (10 seconds)
  }

  private def titleField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#title")) getOrElse fail("Title field not found")
  }

  private def descriptionField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("textarea#description")) getOrElse fail("Description field not found")
  }

  private def visibilityField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("select#visibility")) getOrElse fail("Visibility field not found")
  }

  private def visibilityOption(visibility: Visibility)(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(s"option[value='${visibility.value}']")) getOrElse fail("Visibility option not found")
  }

  private def visibilityRadioInput(visibility: Visibility)(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(s"input[name='visibility'][value='${visibility.value}']"))
      .getOrElse(fail("Visibility option not found"))
  }

  private def templateCard(template: Template)(implicit webDriver: WebDriver): WebElement = eventually {
    findAll(cssSelector("div.template-card > div.card-footer > p"))
      .find(_.text.startsWith(template.name))
      .getOrElse(fail("Target template not found"))
      .parent
  }

  def createButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#create-new-project")) getOrElse fail("Create button not found")
  }
}
