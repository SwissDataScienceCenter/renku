package ch.datascience.graph.json

import ch.datascience.graph.NamespaceAndName
import play.api.libs.json.{JsString, Writes}

/**
  * Created by johann on 17/05/17.
  */
object NamespaceAndNameWrites extends Writes[NamespaceAndName] {

  def writes(namespaceAndName: NamespaceAndName): JsString = JsString(namespaceAndName.asString)

}
