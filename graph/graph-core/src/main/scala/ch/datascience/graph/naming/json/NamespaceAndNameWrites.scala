package ch.datascience.graph.naming.json

import ch.datascience.graph.naming.NamespaceAndName
import play.api.libs.json.{JsString, Writes}

/**
  * Created by johann on 17/05/17.
  */
object NamespaceAndNameWrites extends StringWrites[NamespaceAndName] {

  def writes(namespaceAndName: NamespaceAndName): JsString = JsString(namespaceAndName.asString)

}
