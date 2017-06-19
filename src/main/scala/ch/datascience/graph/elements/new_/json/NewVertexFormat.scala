package ch.datascience.graph.elements.new_.json

import ch.datascience.graph.elements.Vertex
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.detached.json.DetachedRichPropertyFormat
import ch.datascience.graph.elements.json.{VertexReads, VertexWrites}
import ch.datascience.graph.elements.new_.NewVertex
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 31/05/17.
  */

object NewVertexFormat extends Format[NewVertex] {

  def writes(vertex: NewVertex): JsValue = writer.writes(vertex)

  def reads(json: JsValue): JsResult[NewVertex] = reader.reads(json)

  private[this] lazy val writer: Writes[NewVertex] = (
    (JsPath \ "temp_id").write[NewVertex#TempId] and
      JsPath.write[Vertex { type Prop = DetachedRichProperty }](vertexWriter)
  ) { vertex => (vertex.tempId, vertex) }

  private[this] lazy val reader: Reads[NewVertex] = (
    (JsPath \ "temp_id").read[NewVertex#TempId] and
      JsPath.read[Vertex { type Prop = DetachedRichProperty }](vertexReader)
  ) { (tempId, vertex) => NewVertex(tempId, vertex.types, vertex.properties) }

  private[this] lazy val vertexWriter: Writes[Vertex { type Prop = DetachedRichProperty }] = new VertexWrites[DetachedRichProperty]()(DetachedRichPropertyFormat)

  private[this] lazy val vertexReader: Reads[Vertex { type Prop = DetachedRichProperty }] = new VertexReads[DetachedRichProperty]()(DetachedRichPropertyFormat)

}
