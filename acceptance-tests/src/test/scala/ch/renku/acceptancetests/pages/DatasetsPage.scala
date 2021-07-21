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
import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find, findAll}

import scala.annotation.tailrec
import scala.concurrent.duration._

object DatasetsPage
    extends RenkuPage(
      path = s"/datasets",
      title = "Renku"
    )
    with TopBar {

  def searchBox(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("#searchQuery")) getOrElse fail("Datasets -> search query field cannot be found")
  }

  def searchButton(implicit webDriver: WebDriver): WebElement = eventually {
    find(cssSelector("form button.btn.btn-primary")) getOrElse fail("Datasets -> search button cannot be found")
  }

  def searchResultLinks(implicit webDriver: WebDriver): List[WebElement] = eventually {
    findAll(cssSelector("div.col-sm-12.col-md-8 div.card-body a[href^='/datasets/']")).toList
  }

  def maybeBouncer(implicit webDriver: WebDriver): Option[WebElement] = eventually {
    findAll(cssSelector(".bouncer")).toList.headOption
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
}
