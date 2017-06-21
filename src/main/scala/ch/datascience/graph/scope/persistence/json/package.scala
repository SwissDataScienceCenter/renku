package ch.datascience.graph.scope.persistence

import ch.datascience.graph.types.PropertyKey
import play.api.libs.json.{Reads, Writes}
import ch.datascience.graph.naming.json.NamespaceAndNameFormat

/**
  * Created by johann on 24/05/17.
  */
package object json {

  implicit lazy val fetchPropertiesForQueryReads: Reads[Set[PropertyKey#Key]] = new FetchPropertiesForQueryReads
  implicit lazy val fetchPropertiesForQueryWrites: Writes[Set[PropertyKey#Key]] = new FetchPropertiesForQueryWrites
  implicit lazy val fetchPropertiesForResponseReads: Reads[Map[PropertyKey#Key, PropertyKey]] = FetchPropertiesForResponseReads
  implicit lazy val fetchPropertiesForResponseWrites: Writes[Map[PropertyKey#Key, PropertyKey]] = new FetchPropertiesForResponseWrites

}
