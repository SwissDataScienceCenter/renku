package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record, TypedRecord}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 31/05/17.
  */
class TypedRecordWrites[P <: Property : Writes] extends Writes[TypedRecord { type Prop <: P }] {

  def writes(record: TypedRecord {type Prop <: P}): JsValue = self.writes(record)

  private[this] lazy val self: Writes[TypedRecord { type Prop <: P }] = (
    (JsPath \ "types").write[Iterable[TypedRecord#TypeId]] and
      JsPath.write[Record { type Prop <: P }](recordWrites)
    ) { record => (record.types, record) }

  private[this] lazy val recordWrites = new RecordWrites[P]

  private[this] implicit lazy val typeWrites: Writes[TypedRecord#TypeId] = TypeIdFormat

}
