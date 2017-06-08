package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Record, Property, TypedRecord}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 31/05/17.
  */
class TypedRecordReads[P <: Property : Reads] extends Reads[TypedRecord { type Prop = P }] {

  def reads(json: JsValue): JsResult[TypedRecord {type Prop = P}] = self.reads(json)

  private[this] lazy val self: Reads[TypedRecord { type Prop = P }] = (
    (JsPath \ "types").readNullable[Iterable[TypedRecord#TypeId]] and
      JsPath.read[Record { type Prop = P }](recordReads)
    ) { (t, record) =>
    val ts = t match {
      case Some(x) => x
      case _ => Seq.empty[TypedRecord#TypeId]
    }
    new TypedRecord {
      type Prop = P
      def types: Set[TypeId] = ts.toSet
      def properties: Properties = record.properties
    }
  }

  private[this] lazy val recordReads = new RecordReads[P]

  private[this] implicit lazy val typeReads: Reads[TypedRecord#TypeId] = TypeIdFormat

}
