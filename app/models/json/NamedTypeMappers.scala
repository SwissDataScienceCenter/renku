package models.json

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.persistence.model.{GraphDomain, NamedType, RichPropertyKey, RichNamedType}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 16/05/17.
  */
object NamedTypeMappers {

  def namedTypeWrites: Writes[RichNamedType] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomain").write[GraphDomain] and
      (JsPath \ "name").write[String] and
      (JsPath \ "super_types").write[Map[NamespaceAndName, RichNamedType]](superTypesWrites) and
      (JsPath \ "properties").write[Map[NamespaceAndName, RichPropertyKey]](propertiesWrites)
  )(unlift(RichNamedType.unapply))

  def nestedNamedTypeWrites: Writes[RichNamedType] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomain").write[GraphDomain] and
      (JsPath \ "name").write[String]
  )(unlift(RichNamedType.unapply) andThen { t => (t._1, t._2, t._3) })

//  def rowNamedTypeWrites: Writes[NamedType] = (
//    (JsPath \ "id").write[UUID] and
//      (JsPath \ "graphDomainId").write[UUID] and
//      (JsPath \ "name").write[String]
//  )(unlift(NamedType.unapply))
//
//  def rowPropertyKeyWrites: Writes[PropertyKey] = (
//    (JsPath \ "id").write[UUID] and
//      (JsPath \ "graphDomainId").write[UUID] and
//      (JsPath \ "name").write[String] and
//      (JsPath \ "datatype").write[DataType] and
//      (JsPath \ "cardinality").write[Cardinality]
//  )(unlift(PropertyKey.unapply))

  def superTypesWrites: Writes[Map[NamespaceAndName, RichNamedType]] = new Writes[Map[NamespaceAndName, RichNamedType]] {
    private[this] implicit lazy val valueWrites: Writes[RichNamedType] = nestedNamedTypeWrites
//    private[this] def mapW = implicitly[Writes[Map[String, RichNamedType]]]
    private[this] def mapW = Writes.mapWrites[RichNamedType](valueWrites)
    def writes(map: Map[NamespaceAndName, RichNamedType]): JsValue = mapW.writes(map.map({ case (k,v) => k.asString -> v }))
  }

  def propertiesWrites: Writes[Map[NamespaceAndName, RichPropertyKey]] = new Writes[Map[NamespaceAndName, RichPropertyKey]] {
    private[this] implicit lazy val valueWrites: Writes[RichPropertyKey] = PropertyKeyMappers.propertyKeyWrites
//    private[this] def mapW = implicitly[Writes[Map[String, RichPropertyKey]]]
    private[this] def mapW = Writes.mapWrites[RichPropertyKey](valueWrites)
    def writes(map: Map[NamespaceAndName, RichPropertyKey]): JsValue = mapW.writes(map.map({ case (k,v) => k.asString -> v }))
  }
//
//  def propertiesWrites2: Writes[Map[NamespaceAndName, PropertyKey]] = new Writes[Map[NamespaceAndName, PropertyKey]] {
//    private[this] implicit lazy val propertyKeyWritesV: Writes[PropertyKey] = PropertyKeyMappers.propertyKeyWrites
//    private[this] def mapWrites2 = implicitly[Writes[Map[String, PropertyKey]]](Writes.mapWrites(propertyKeyWritesV))
//    def writes(map: Map[NamespaceAndName, PropertyKey]): JsValue = mapWrites2.writes(map.map({ case (k,v) => k.asString -> v }))
//  }

}
