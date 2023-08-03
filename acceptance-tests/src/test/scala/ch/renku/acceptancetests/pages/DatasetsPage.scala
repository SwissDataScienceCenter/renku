/*
 * Copyright 2023 Swiss Data Science Center (SDSC)
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
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}

import scala.annotation.tailrec
import scala.concurrent.duration._

object DatasetsPage extends RenkuPage(path = s"/search?type=dataset") with TopBar {

  def searchBox(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("input[type=search]")) getOrElse fail("Search -> search query field cannot be found")
  }

  def searchButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#addon-wrapping")) getOrElse fail("Search -> search button cannot be found")
  }

  def sortByDropdownMenu(
      currentOrdering: DatasetSearchOrdering = DatasetSearchOrdering.BestMatch
  )(implicit webDriver: WebDriver): WebElement =
    eventually {
      find(cssSelector("select.sorting-input")) getOrElse fail(
        s"Search -> sort ordering button not found"
      )
    }

  def sortByDropdownItem(datasetOrdering: DatasetSearchOrdering)(implicit webDriver: WebDriver): WebElement =
    eventually {
      findAll(cssSelector("select.sorting-input > option")).find(elem =>
        elem.getText == datasetOrdering.value
      ) getOrElse fail(
        s"Search -> sort by ${datasetOrdering.value} button not found"
      )
    }

  def searchResultLinks(implicit webDriver: WebDriver): List[WebElement] = eventually {
    findAll(cssSelector(".card-title")).toList
  }

  override def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement] = {
    waitIfBouncing
    Some(searchResultLinks.headOption getOrElse searchBox)
  }

  @tailrec def waitIfBouncing(implicit webDriver: WebDriver): Unit = maybeBouncer match {
    case Some(_) =>
      sleep(1 second)
      waitIfBouncing
    case _ =>
      sleep(1 second)
  }
  trait DatasetSearchOrdering {
    def value: String
  }
  object DatasetSearchOrdering {
    case object Title extends DatasetSearchOrdering {
      val value = "Title: A to Z"
    }
    case object Date extends DatasetSearchOrdering {
      val value = "Recently modified"
    }
    case object BestMatch extends DatasetSearchOrdering {
      val value = "Best match"
    }
  }
}
