package ch.datascience.graph.elements.detached.json

import ch.datascience.graph.elements.{RichProperty, Vertex}
import ch.datascience.graph.elements.detached.{DetachedProperty, DetachedRichProperty, DetachedVertex}
import ch.datascience.graph.elements.json.{RichPropertyReads, RichPropertyWrites, VertexReads, VertexWrites}
import play.api.libs.json._

/**
  * Created by johann on 31/05/17.
  */
object DetachedVertexFormat extends Format[DetachedVertex] {

  def writes(vertex: DetachedVertex): JsValue = writer.writes(vertex)

  def reads(json: JsValue): JsResult[DetachedVertex] = for {
    vertex <- reader.reads(json)
  } yield DetachedVertex(vertex.types, vertex.properties)

  private[this] lazy val writer: Writes[Vertex { type Prop = DetachedRichProperty }] = new VertexWrites[DetachedRichProperty]()(DetachedRichPropertyFormat)

  private[this] lazy val reader: Reads[Vertex { type Prop = DetachedRichProperty }] = new VertexReads[DetachedRichProperty]()(DetachedRichPropertyFormat)

}
