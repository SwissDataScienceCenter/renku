package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{RichProperty, Vertex}
import play.api.libs.json.{JsResult, JsValue, Reads}

/**
  * Created by johann on 31/05/17.
  */
class VertexReads[P <: RichProperty : Reads] extends Reads[Vertex { type Prop = P }] {

  def reads(json: JsValue): JsResult[Vertex {type Prop = P}] = for {
    record <- recordReads.reads(json)
  } yield new Vertex {
    type Prop = P
    def types: Set[TypeId] = record.types
    def properties: Properties = record.properties
  }

  private[this] lazy val recordReads: TypedMultiRecordReads[P] = new TypedMultiRecordReads[P]

}
