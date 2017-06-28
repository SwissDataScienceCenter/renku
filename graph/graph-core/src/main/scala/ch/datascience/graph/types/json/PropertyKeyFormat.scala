package ch.datascience.graph.types.json

import ch.datascience.graph.types.{DataType, PropertyKey, Cardinality}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 19/06/17.
  */
object PropertyKeyFormat extends Format[PropertyKey] {

  def writes(propertyKey: PropertyKey): JsValue = writer.writes(propertyKey)

  def reads(json: JsValue): JsResult[PropertyKey] = reader.reads(json)

  private[this] def writer: Writes[PropertyKey] = (
    (JsPath \ "key").write[PropertyKey#Key](propKeyFormat) and
      (JsPath \ "data_type").write[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").write[Cardinality](CardinalityFormat)
    )(unlift(PropertyKey.unapply))

  private[this] def reader: Reads[PropertyKey] = (
    (JsPath \ "key").read[PropertyKey#Key](propKeyFormat) and
      (JsPath \ "data_type").read[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").read[Cardinality](CardinalityFormat)
    )(PropertyKey.apply _)
}
