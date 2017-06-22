package ch.datascience.graph.init

import ch.datascience.graph.Constants
import ch.datascience.graph.types._
import ch.datascience.graph.types.json.{CardinalityFormat, DataTypeFormat, NamedTypeFormat, PropertyKeyFormat, EdgeLabelFormat}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 21/06/17.
  */
object TypeInitFormat extends Format[TypeInit] {

  def reads(json: JsValue): JsResult[TypeInit] = reader.reads(json)

  def writes(typeInit: TypeInit): JsValue = writer.writes(typeInit)

  private[this] lazy val writer: Writes[TypeInit] = (
    (JsPath \ "system_property_keys").write[List[SystemPropertyKey]] and
      (JsPath \ "property_keys").write[List[PropertyKey]] and
      (JsPath \ "named_types").write[List[NamedType]] and
      (JsPath \ "edge_labels").write[List[EdgeLabel]]
    )(unlift(TypeInit.unapply))

  private[this] lazy val reader: Reads[TypeInit] = (
    (JsPath \ "system_property_keys").readNullable[List[SystemPropertyKey]].map(_.getOrElse(List.empty)) and
      (JsPath \ "property_keys").readNullable[List[PropertyKey]].map(_.getOrElse(List.empty)) and
      (JsPath \ "named_types").readNullable[List[NamedType]].map(_.getOrElse(List.empty)) and
      (JsPath \ "edge_labels").readNullable[List[EdgeLabel]].map(_.getOrElse(List.empty))
    )(TypeInit.apply _)

  private[this] implicit lazy val SystemPropertyKeyFormat: Format[SystemPropertyKey] = (
    (JsPath \ "name").format[String] and
      (JsPath \ "data_type").format[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").format[Cardinality](CardinalityFormat)
  )(SystemPropertyKey.apply, unlift(SystemPropertyKey.unapply))

  private[this] implicit lazy val propertyKeyFormat: Format[PropertyKey] = PropertyKeyFormat
  private[this] implicit lazy val namedTypeFormat: Format[NamedType] = NamedTypeFormat
  private[this] implicit lazy val edgeLabelFormat: Format[EdgeLabel] = EdgeLabelFormat

}
