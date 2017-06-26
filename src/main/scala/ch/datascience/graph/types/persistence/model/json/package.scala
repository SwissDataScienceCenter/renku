package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.{Cardinality, DataType, Multiplicity}
import play.api.libs.json.{Format, Reads}

/**
  * Created by johann on 13/06/17.
  */
package object json {

  implicit lazy val UUIDFormat: Format[UUID] = UUIDMappers.UUIDFormat
  lazy val notUUidReads: Reads[String] = UUIDMappers.notUUidReads

  implicit lazy val GraphDomainFormat: Format[GraphDomain] = GraphDomainMappers.GraphDomainFormat
  lazy val GraphDomainRequestFormat: Format[String] = GraphDomainMappers.GraphDomainRequestFormat

  implicit lazy val PropertyKeyFormat: Format[RichPropertyKey] = PropertyKeyMappers.PropertyKeyFormat
  lazy val PropertyKeyRequestFormat: Format[(String, String, DataType, Cardinality)] = PropertyKeyMappers.PropertyKeyRequestFormat

  implicit lazy val SystemPropertyKeyFormat: Format[SystemPropertyKey] = SystemPropertyKeyMappers.SystemPropertyKeyFormat
  lazy val SystemPropertyKeyRequestFormat: Format[(String, DataType, Cardinality)] = SystemPropertyKeyMappers.SystemPropertyKeyRequestFormat

  implicit lazy val EdgeLabelFormat: Format[RichEdgeLabel] = EdgeLabelMappers.EdgeLabelFormat
  lazy val EdgeLabelRequestFormat: Format[(String, String, Multiplicity)] = EdgeLabelMappers.EdgeLabelRequestFormat

  implicit lazy val NamedTypeFormat: Format[RichNamedType] = NamedTypeMappers.NamedTypeFormat
  lazy val NamedTypeRequestFormat: Format[(String, String, Seq[NamespaceAndName], Seq[NamespaceAndName])] = NamedTypeMappers.NamedTypeRequestFormat

}
