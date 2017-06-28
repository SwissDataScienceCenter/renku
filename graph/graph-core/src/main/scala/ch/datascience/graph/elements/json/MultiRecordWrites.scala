package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{MultiPropertyValue, MultiRecord, Property}
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 31/05/17.
  */
class MultiRecordWrites[P <: Property : Writes] extends Writes[MultiRecord { type Prop <: P }] {

  def writes(record: MultiRecord { type Prop <: P }): JsValue = (JsPath \ "properties").write[record.Properties].writes(record.properties)

//  private[this] implicit lazy val mapWrites: Writes[Map[P#Key, MultiPropertyValue[P]]] = KeyFormat.mapWrites[MultiPropertyValue[P]](multiPropertyValueWrites)
  private[this] implicit lazy val mapWrites: Writes[Map[P#Key, MultiPropertyValue[P]]] = new Writes[Map[P#Key, MultiPropertyValue[P]]] {
    def writes(m: Map[P#Key, MultiPropertyValue[P]]): JsValue = implicitly[Writes[Iterable[MultiPropertyValue[P]]]].writes(m.values)
  }

  private[this] implicit lazy val multiPropertyValueWrites: MultiPropertyValueWrites[P] = new MultiPropertyValueWrites[P]

}
