package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.pages.Page._
import ch.renku.acceptancetests.pages.RenkuPage.RenkuBaseUrl
import ch.renku.acceptancetests.tooling._
import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string._
import org.openqa.selenium.{By, WebElement}
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers => ScalatestMatchers}
import org.scalatestplus.selenium.WebBrowser

import scala.concurrent.duration.Duration
import scala.language.implicitConversions

abstract class Page[Url <: BaseUrl] extends ScalatestMatchers with Eventually with AcceptanceSpecPatience {

  val path:  Path
  val title: Title
  def url(implicit baseUrl: Url): String = s"$baseUrl$path"

  protected implicit def toWebElement(element: WebBrowser.Element): WebElement =
    element.underlying
  protected implicit def toMaybeWebElement(maybeElement: Option[WebBrowser.Element]): Option[WebElement] =
    maybeElement.map(_.underlying)

  protected implicit class WebElementOps(element: WebBrowser.Element) {
    def parent: WebElement = element.findElement(By.xpath("./.."))
  }

  protected implicit class OperationOps(unit: Unit) {
    def sleep(duration: Duration): Unit =
      Thread sleep duration.toMillis
  }
}

object Page {
  type Path  = String Refined StartsWith[W.`"/"`.T]
  type Title = String Refined NonEmpty
}

abstract class RenkuPage extends Page[RenkuBaseUrl]

object RenkuPage {
  case class RenkuBaseUrl(value: String Refined Url) extends BaseUrl(value)
}
