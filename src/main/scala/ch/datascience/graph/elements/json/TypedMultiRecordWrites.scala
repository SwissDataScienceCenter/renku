package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{MultiRecord, Property, TypedMultiRecord}
import play.api.libs.json.{JsPath, JsValue, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 31/05/17.
  */
class TypedMultiRecordWrites[P <: Property : Writes] extends Writes[TypedMultiRecord { type Prop <: P }] {

  def writes(record: TypedMultiRecord {type Prop <: P}): JsValue = self.writes(record)

  private[this] lazy val self: Writes[TypedMultiRecord { type Prop <: P }] = (
    (JsPath \ "types").write[Iterable[TypedMultiRecord#TypeId]] and
      JsPath.write[MultiRecord { type Prop <: P }](recordWrites)
    ) { record => (record.types, record) }

  private[this] lazy val recordWrites = new MultiRecordWrites[P]

  private[this] implicit lazy val typeWrites: Writes[TypedMultiRecord#TypeId] = TypeIdFormat

}
