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

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.pages.Page.{Path, Title}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalactic.source
import scala.concurrent.duration._
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

object DatasetPage {
  def apply(datasetName: DatasetName)(implicit projectPage: ProjectPage) = new DatasetPage(datasetName, projectPage)
}

class DatasetPage(datasetName: DatasetName, projectPage: ProjectPage) extends RenkuPage with TopBar {

  override val title: Title = "Renku"
  override val path: Path = Refined.unsafeApply(
    s"${projectPage.path}/datasets/$datasetName"
  )

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = Some(datasetTitle)

  def datasetTitle(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector(".col-md-8 > h4")) getOrElse fail("Dataset -> Dataset title not found")
  }

  def modifyButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("a[href='modify']")) getOrElse fail("Dataset -> Modify button not found")
  }

  object ProjectsList {
    def link(to: ProjectPage)(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector(s"td a[href='${to.path}' i]"))
        .getOrElse(fail(s"Project '${to.path}' not found"))
    }(waitUpTo(20 seconds), implicitly[source.Position])
  }

  object ModificationForm {

    def formTitle(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("h3.uk-heading-divider")) getOrElse fail("Form title not found")
    }

    def datasetTitleField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input[name='title']")) getOrElse fail("Dataset title field not found")
    }

    def datasetNameField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("input[name='name']")) getOrElse fail("Dataset name field not found")
    }

    def datasetDescriptionField(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("div[role='textbox']")) getOrElse fail("Dataset description field not found")
    }

    def datasetSubmitButton(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("button[type='submit']")) getOrElse fail("Dataset form submit button not found")
    }
  }
}
