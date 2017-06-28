package ch.datascience.graph.elements.new_.json

import ch.datascience.graph.elements.Edge
import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.detached.json.DetachedPropertyFormat
import ch.datascience.graph.elements.json.{EdgeReads, EdgeWrites}
import ch.datascience.graph.elements.new_.NewEdge
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object NewEdgeFormat extends Format[NewEdge] {

  def writes(edge: NewEdge): JsValue = edgeWriter.writes(edge)

  def reads(json: JsValue): JsResult[NewEdge] = edgeReader.reads(json).map { edge =>
    NewEdge(edge.label, edge.from, edge.to, edge.properties)
  }

  private[this] type VR = Either[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id]

  private[this] lazy val edgeWriter: Writes[Edge { type VertexReference = VR; type Prop = DetachedProperty }] = new EdgeWrites[VR, DetachedProperty]()(VertexReferenceWrites, DetachedPropertyFormat)

  private[this] lazy val edgeReader: Reads[Edge { type VertexReference = VR; type Prop = DetachedProperty }] = new EdgeReads[VR, DetachedProperty]()(VertexReferenceReads, DetachedPropertyFormat)


}
