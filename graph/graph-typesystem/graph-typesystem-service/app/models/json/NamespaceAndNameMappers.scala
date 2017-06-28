package models.json

import ch.datascience.graph.naming.NamespaceAndName
import play.api.libs.json._

/**
  * Created by johann on 16/05/17.
  */
object NamespaceAndNameMappers {

  def namespaceAndNameWrites: Writes[NamespaceAndName] = new Writes[NamespaceAndName] {
    def writes(key: NamespaceAndName): JsString = JsString(key.asString)
  }

  def namespaceAndNameReads: Reads[NamespaceAndName] = new Reads[NamespaceAndName] {
    def reads(json: JsValue): JsResult[NamespaceAndName] = json.validate[String] flatMap { str =>
      try {
        JsSuccess(NamespaceAndName(str))
      } catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }
  }

}
