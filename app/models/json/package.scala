package models

import java.util.UUID

import ch.datascience.typesystem.relationaldb.row.GraphDomain
import play.api.libs.json._

/**
  * Created by johann on 25/04/17.
  */
package object json {

  implicit lazy val uuidReads: Reads[UUID] = UUIDMappers.uuidReads
  implicit lazy val uuidWrites: Writes[UUID] = UUIDMappers.uuidWrites

  implicit lazy val graphDomainReads: Reads[GraphDomain] = GraphDomainMappers.graphDomainReads
  implicit lazy val graphDomainWrites: Writes[GraphDomain] = GraphDomainMappers.graphDomainWrites

}
