package ch.datascience.graph.types

import ch.datascience.graph.naming.json.{NamespaceAndNameReads, NamespaceAndNameWrites}
import play.api.libs.json.{Reads, Writes}

/**
  * Created by johann on 23/05/17.
  */
package object json {

  implicit lazy val standardPropKeyReads: Reads[StandardPropKey] = NamespaceAndNameReads
  implicit lazy val standardPropKeyWrites: Writes[StandardPropKey] = NamespaceAndNameWrites

  implicit lazy val standardPropertyKeyReads: Reads[StandardPropertyKey] = new PropertyKeyReads[StandardPropKey]
  implicit lazy val standardPropertyKeyWrites: Writes[StandardPropertyKey] = new PropertyKeyWrites[StandardPropKey]

}
