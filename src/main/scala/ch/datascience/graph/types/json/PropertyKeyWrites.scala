package ch.datascience.graph.types.json

import ch.datascience.graph.types.{Cardinality, DataType, PropertyKey}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 17/05/17.
  */
class PropertyKeyWrites[-Key : Writes] extends Writes[PropertyKey[Key]] {

  def writes(propertyKey: PropertyKey[Key]): JsValue = self.writes(propertyKey)

  private[this] lazy val self: Writes[PropertyKey[Key]] = makeSelf

  private[this] def makeSelf: Writes[PropertyKey[Key]] = (
    (JsPath \ "key").write[Key] and
      (JsPath \ "dataType").write[DataType](DataTypeWrites) and
      (JsPath \ "cardinality").write[Cardinality](CardinalityWrites)
    )(unlift(PropertyKey.unapply[Key]))

}
