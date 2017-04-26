package models

import java.util.UUID

import ch.datascience.typesystem.model.{Cardinality, DataType, GraphDomain, PropertyKey}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 25/04/17.
  */
package object json {

  implicit lazy val uuidReads: Reads[UUID] = UUIDMappers.uuidReads
  implicit lazy val uuidWrites: Writes[UUID] = UUIDMappers.uuidWrites
  lazy val notUUIDReads: Reads[String] = UUIDMappers.notUUidReads

  implicit lazy val dataTypeReads: Reads[DataType] = DataTypeMappers.dataTypeReads
  implicit lazy val dataTypeWrites: Writes[DataType] = DataTypeMappers.dataTypeWrites

  implicit lazy val cardinalityReads: Reads[Cardinality] = CardinalityMappers.cardinalityReads
  implicit lazy val cardinalityWrites: Writes[Cardinality] = CardinalityMappers.cardinalityWrites

  implicit lazy val graphDomainReads: Reads[GraphDomain] = GraphDomainMappers.graphDomainReads
  implicit lazy val graphDomainWrites: Writes[GraphDomain] = GraphDomainMappers.graphDomainWrites

  implicit lazy val propertyKeyWrites: Writes[PropertyKey] = PropertyKeyMappers.propertyKeyWrites


  lazy val namespaceReads: Reads[String] = JsPath.read[String](Reads.pattern("([^:]*)".r) <~ notUUIDReads)
  lazy val nameReads: Reads[String] = JsPath.read[String](Reads.pattern("([^:]+)".r) <~ notUUIDReads)

}
