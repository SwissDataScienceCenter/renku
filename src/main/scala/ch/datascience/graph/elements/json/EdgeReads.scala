package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Edge, Property, Record}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 08/06/17.
  */
class EdgeReads[V : Reads, P <: Property : Reads] extends Reads[Edge { type VertexReference = V; type Prop = P }] {

  def reads(json: JsValue): JsResult[Edge {type VertexReference = V; type Prop = P}] = self.reads(json)

  private[this] lazy val self: Reads[Edge { type VertexReference = V; type Prop = P }] = (
    (JsPath \ "label").read[Edge#Label] and
      (JsPath \ "from").read[V] and
      (JsPath \ "to").read[V] and
      JsPath.read[Record { type Prop = P }](recordReads)
  ) { (l, f, t, record) =>
    new Edge {
      type VertexReference = V
      type Prop = P
      def label: Label = l
      def from: V = f
      def to: V = t
      def properties: Properties = record.properties
    }
  }

  private[this] lazy val recordReads = new RecordReads[P]

  private[this] implicit lazy val labelReads: Reads[Edge#Label] = EdgeLabelFormat

}
