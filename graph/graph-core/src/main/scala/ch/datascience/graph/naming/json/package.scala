package ch.datascience.graph.naming

import play.api.libs.json.{Format, Reads, Writes}

/**
  * Created by johann on 24/05/17.
  */
package object json {

  implicit lazy val NamespaceAndNameFormat: StringFormat[NamespaceAndName] = StringFormat(NamespaceAndNameReads, NamespaceAndNameWrites)

  lazy val NamespaceFormat: Format[String] = Format(NamespaceReads, implicitly[Writes[String]])

  lazy val NameFormat: Format[String] = Format(NameReads, implicitly[Writes[String]])

}
