package ch.datascience.graph.naming.json

import ch.datascience.graph.naming.NamespaceAndName
import play.api.libs.json._

/**
  * Created by johann on 17/05/17.
  */
object NamespaceAndNameReads extends StringReads[NamespaceAndName] {

  def reads(jsString: JsString): JsResult[NamespaceAndName] = {
    val str = jsString.value

    try {
      JsSuccess(NamespaceAndName(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
