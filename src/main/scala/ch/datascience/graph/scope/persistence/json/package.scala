package ch.datascience.graph.scope.persistence

import ch.datascience.graph.types.{StandardPropKey, StandardPropertyKey}
import play.api.libs.json.{Reads, Writes}
import ch.datascience.graph.types.json.{standardPropKeyReads, standardPropKeyWrites}

/**
  * Created by johann on 24/05/17.
  */
package object json {

  implicit lazy val standardFetchPropertiesForQueryReads: Reads[Set[StandardPropKey]] = new FetchPropertiesForQueryReads[StandardPropKey]
  implicit lazy val standardFetchPropertiesForQueryWrites: Writes[Set[StandardPropKey]] = new FetchPropertiesForQueryWrites[StandardPropKey]
  implicit lazy val standardFetchPropertiesForResponseReads: Reads[Map[StandardPropKey, StandardPropertyKey]] = new FetchPropertiesForResponseReads[StandardPropKey]
  implicit lazy val standardFetchPropertiesForResponseWrites: Writes[Map[StandardPropKey, StandardPropertyKey]] = new FetchPropertiesForResponseWrites[StandardPropKey]

}
