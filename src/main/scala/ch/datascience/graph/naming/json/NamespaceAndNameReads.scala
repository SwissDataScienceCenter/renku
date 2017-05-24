package ch.datascience.graph.naming.json

import ch.datascience.graph.naming.NamespaceAndName
import play.api.libs.json._

/**
  * Created by johann on 17/05/17.
  */
object NamespaceAndNameReads extends Reads[NamespaceAndName] {

  def reads(json: JsValue): JsResult[NamespaceAndName] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(NamespaceAndName(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
