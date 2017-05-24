package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record, RichProperty}
import play.api.libs.json.{JsPath, JsValue, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 24/05/17.
  */
class RichPropertyWrites[Key : StringWrites, Value : Writes, MetaValue, MetaProp <: Property[Key, MetaValue] : Writes] extends Writes[RichProperty[Key, Value, MetaValue, MetaProp]] {

  def writes(property: RichProperty[Key, Value, MetaValue, MetaProp]): JsValue = self.writes(property)

  private[this] lazy val self: Writes[RichProperty[Key, Value, MetaValue, MetaProp]] = (
    (JsPath \ "value").write[Value] and
    JsPath.write[Record[Key, MetaValue, MetaProp]]
  ) { property: RichProperty[Key, Value, MetaValue, MetaProp] => (property.value, property) }

  private[this] implicit lazy val recordWrites: Writes[Record[Key, MetaValue, MetaProp]] = new RecordWrites[Key, MetaValue, MetaProp]

}
