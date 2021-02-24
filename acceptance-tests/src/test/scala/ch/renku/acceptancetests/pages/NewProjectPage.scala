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

package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.model.projects.{ProjectDetails, Template, Visibility}
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import ch.renku.acceptancetests.tooling.DocsScreenshots
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

import scala.concurrent.duration._

case object NewProjectPage extends RenkuPage with TopBar {

  override val path:  Path  = "/projects/new"
  override val title: Title = "Renku"

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(templateField)

  def submitFormWith(
      project:          ProjectDetails
  )(implicit webDriver: WebDriver, docsScreenshots: DocsScreenshots): Unit = eventually {
    titleField.clear() sleep (5 seconds)
    titleField.enterValue(project.title.value) sleep (1 second)

    visibilityField.click() sleep (1 second)
    visibilityOption(project.visibility).click() sleep (1 second)

    templateField.click() sleep (1 second)
    templateOption(project.template).click() sleep (1 second)
    templateField.click() sleep (5 second)

    descriptionField.clear() sleep (1 second)
    descriptionField.enterValue(project.description.value) sleep (1 second)

    docsScreenshots.takeScreenshot()

    createButton.click() sleep (5 seconds)
  }

  private def titleField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#title")) getOrElse fail("Title field not found")
  }

  private def visibilityField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("select#visibility")) getOrElse fail("Visibility field not found")
  }

  private def visibilityOption(visibility: Visibility)(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(s"option[value='${visibility.value}']")) getOrElse fail("Visibility option not found")
  }

  private def templateField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("select#template")) getOrElse fail("Template field not found ")
  }

  private def templateOption(template: Template)(implicit webDriver: WebDriver): WebElement =
    eventually {
      find(cssSelector(s"option[value='${template.name}']")) getOrElse fail("Template option not found")
    }

  private def descriptionField(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input#parameter-description")) getOrElse fail("Description parameter field not found")
  }

  def createButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#create-new-project")) getOrElse fail("Create button not found")
  }
}
