package ch.datascience.graph.types

import ch.datascience.graph.naming.json.NamespaceAndNameFormat
import play.api.libs.json.Format

/**
  * Created by johann on 23/05/17.
  */
package object json {

//  lazy val propKeyReads: Reads[PropertyKey#Key] = NamespaceAndNameReads
//  lazy val propKeyWrites: Writes[PropertyKey#Key] = NamespaceAndNameWrites

  lazy val propKeyFormat: Format[PropertyKey#Key] = NamespaceAndNameFormat

//  implicit lazy val propertyKeyReads: Reads[PropertyKey] = new PropertyKeyReads
//  implicit lazy val propertyKeyWrites: Writes[PropertyKey] = new PropertyKeyWrites

}
