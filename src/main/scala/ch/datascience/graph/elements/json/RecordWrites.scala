package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Properties, Property, Record}
import play.api.libs.json.{JsPath, JsValue, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 24/05/17.
  */
class RecordWrites[Key : StringWrites, Value, Prop <: Property[Key, Value] : Writes] extends Writes[Record[Key, Value, Prop]] {

  def writes(record: Record[Key, Value, Prop]): JsValue = (JsPath \ "properties").write[Properties[Key, Value, Prop]].writes(record.properties)

  private[this] implicit lazy val mapWrites: Writes[Properties[Key, Value, Prop]] = implicitly[StringWrites[Key]].mapWrites[Prop]

}
