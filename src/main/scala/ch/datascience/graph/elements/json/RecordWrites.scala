package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record}
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 24/05/17.
  */
class RecordWrites[P <: Property : Writes] extends Writes[Record { type Prop <: P }] {

  def writes(record: Record { type Prop <: P }): JsValue = (JsPath \ "properties").write[record.Properties].writes(record.properties)

//  private[this] implicit lazy val mapWrites: Writes[Map[P#Key, P]] = KeyFormat.mapWrites[P](implicitly[Writes[P]])
  private[this] implicit lazy val mapWrites: Writes[Map[P#Key, P]] = new Writes[Map[P#Key, P]] {
    def writes(m: Map[P#Key, P]): JsValue = implicitly[Writes[Iterable[P]]].writes(m.values)
  }

}
