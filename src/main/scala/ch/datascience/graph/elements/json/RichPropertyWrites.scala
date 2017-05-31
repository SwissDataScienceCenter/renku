package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record, RichProperty}
import play.api.libs.json.{JsPath, JsValue, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 24/05/17.
  */
class RichPropertyWrites[P <: Property : Writes] extends Writes[RichProperty { type Prop <: P }] {

  def writes(prop: RichProperty {type Prop <: P}): JsValue = self.writes(prop)

  private[this] lazy val self: Writes[RichProperty { type Prop <: P }] = (
    JsPath.write[Property](PropertyFormat) and
    JsPath.write[Record { type Prop <: P }](recordWrites)
  ) { prop => (prop, prop) }

  private[this] lazy val recordWrites = new RecordWrites[P]

}
