package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record, RichProperty}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 30/05/17.
  */
class RichPropertyReads[P <: Property : Reads] extends Reads[RichProperty { type Prop = P }] {

  def reads(json: JsValue): JsResult[RichProperty {type Prop = P}] = self.reads(json)

  private[this] lazy val self: Reads[RichProperty { type Prop = P }] = (
    JsPath.read[Property](PropertyFormat) and
      JsPath.read[Record { type Prop = P }](recordReads)
    ) { (prop, record) =>
    new RichProperty {
      type Prop = P
      def key: Key = prop.key
      def value: Value = prop.value
      def properties: Properties = record.properties
    }
  }

  private[this] lazy val recordReads = new RecordReads[P]

}
