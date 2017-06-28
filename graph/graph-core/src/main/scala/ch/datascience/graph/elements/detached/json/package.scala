package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.json._
import play.api.libs.json.{Format, JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 30/05/17.
  */
package object json {

  implicit lazy val detachedPropertyFormat: Format[DetachedProperty] = DetachedPropertyFormat

  implicit lazy val detachedRichPropertyFormat: Format[DetachedRichProperty] = DetachedRichPropertyFormat

}
