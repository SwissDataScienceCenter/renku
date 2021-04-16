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

import ch.renku.acceptancetests.model.{BaseUrl, RenkuBaseUrl}
import ch.renku.acceptancetests.tooling._
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.{Matchers => ScalatestMatchers}
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.selenium.WebBrowser

import scala.concurrent.duration._
import scala.language.implicitConversions

abstract class Page[Url <: BaseUrl](val path: String, val title: String)
    extends ScalatestMatchers
    with WebElementOps
    with Eventually
    with AcceptanceSpecPatience {

  require(path.trim.nonEmpty, s"$getClass cannot have empty path")
  require(path startsWith "/", s"$getClass path has to start with '/'")
  require(title.trim.nonEmpty, s"$getClass cannot have empty title")

  def pageReadyElement(implicit webDriver: WebDriver): Option[WebElement]
  def url(implicit baseUrl:                Url): String = s"$baseUrl$path"

  protected implicit def toWebElement(element: WebBrowser.Element): WebElement =
    element.underlying

  protected implicit def toMaybeWebElement(maybeElement: Option[WebBrowser.Element]): Option[WebElement] =
    maybeElement.map(_.underlying)

  protected implicit def toListWebElement(elements: Iterable[WebBrowser.Element]): List[WebElement] =
    elements.map(_.underlying).toList

  protected implicit class ElementOps(element: WebBrowser.Element) {
    lazy val parent: WebElement = element.findElement(By xpath "./..")
  }

  object sleep {
    def apply(duration: Duration): Unit = Page.SleepThread(duration)
  }

  protected implicit class OperationOps(unit: Unit) {
    def sleep(duration: Duration): Unit = Page.SleepThread(duration)
  }

  protected def waitUpTo(duration: Duration): PatienceConfig = PatienceConfig(
    timeout = scaled(Span(AcceptanceSpecPatience.WAIT_SCALE * duration.toSeconds, Seconds)),
    interval = scaled(Span(1, Seconds))
  )
}

object Page {

  // Use a unique name to avoid problems on case-insensitive and preserving file systems
  object SleepThread {
    def apply(duration: Duration): Unit = Thread sleep duration.toMillis
  }
}

abstract class RenkuPage(path: String, title: String) extends Page[RenkuBaseUrl](path, title)
