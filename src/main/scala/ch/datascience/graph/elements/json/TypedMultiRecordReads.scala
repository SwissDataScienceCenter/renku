package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{MultiRecord, Property, TypedMultiRecord}
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 31/05/17.
  */
class TypedMultiRecordReads[P <: Property : Reads] extends Reads[TypedMultiRecord { type Prop = P }] {

  def reads(json: JsValue): JsResult[TypedMultiRecord {type Prop = P}] = self.reads(json)

  private[this] lazy val self: Reads[TypedMultiRecord { type Prop = P }] = (
    (JsPath \ "types").readNullable[Iterable[TypedMultiRecord#TypeId]] and
      JsPath.read[MultiRecord { type Prop = P }](recordReads)
    ) { (t, record) =>
    val ts = t match {
      case Some(x) => x
      case _ => Seq.empty[TypedMultiRecord#TypeId]
    }
    new TypedMultiRecord {
      type Prop = P
      def types: Set[TypeId] = ts.toSet
      def properties: Properties = record.properties
    }
  }

  private[this] lazy val recordReads = new MultiRecordReads[P]

  private[this] implicit lazy val typeReads: Reads[TypedMultiRecord#TypeId] = TypeIdFormat

}
