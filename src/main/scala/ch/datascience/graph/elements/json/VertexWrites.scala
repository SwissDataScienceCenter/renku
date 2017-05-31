package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{RichProperty, Vertex}
import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 31/05/17.
  */
class VertexWrites[P <: RichProperty : Writes] extends Writes[Vertex { type Prop <: P }] {

  def writes(vertex: Vertex {type Prop <: P}): JsValue = self.writes(vertex)

  private[this] lazy val self: TypedMultiRecordWrites[P] = new TypedMultiRecordWrites[P]

}
