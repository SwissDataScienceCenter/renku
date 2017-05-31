package ch.datascience.graph.naming

import play.api.libs.json.Format

/**
  * Created by johann on 24/05/17.
  */
package object json {

  implicit lazy val NamespaceAndNameFormat: StringFormat[NamespaceAndName] = StringFormat(NamespaceAndNameReads, NamespaceAndNameWrites)

}
