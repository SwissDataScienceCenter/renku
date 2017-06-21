package ch.datascience.graph.elements.persisted.json

import ch.datascience.graph.elements.json.{EdgeReads, EdgeWrites, PropertyFormat}
import ch.datascience.graph.elements.persisted.{Path, PersistedEdge, PersistedRecordProperty, PersistedVertex}
import ch.datascience.graph.elements.{Edge, Property}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 13/06/17.
  */
object PersistedEdgeFormat extends Format[PersistedEdge] {

  def writes(edge: PersistedEdge): JsValue = writer.writes(edge)

  def reads(json: JsValue): JsResult[PersistedEdge] = reader.reads(json)

  private[this] lazy val writer: Writes[PersistedEdge] = (
    (JsPath \ "id").write[PersistedEdge#Id] and
      JsPath.write[Edge { type VertexReference = VR; type Prop = PersistedRecordProperty }](edgeWriter)
  ) { edge => (edge.id, edge) }

  private[this] lazy val reader: Reads[PersistedEdge] = (
    (JsPath \ "id").read[PersistedEdge#Id] and
      JsPath.read[Edge { type VertexReference = VR; type Prop = PersistedRecordProperty }](edgeReader)
    ) { (id, edge) => PersistedEdge(id, edge.label, edge.from, edge.to, edge.properties) }

  private[this] type VR = PersistedVertex#Id

  private[this] lazy val edgeWriter: Writes[Edge { type VertexReference = VR; type Prop = PersistedRecordProperty }] = new EdgeWrites[VR, PersistedRecordProperty]()(implicitly[Writes[PersistedVertex#Id]], PersistedRecordPropertyFormat)

  private[this] lazy val edgeReader: Reads[Edge { type VertexReference = VR; type Prop = PersistedRecordProperty }] = new EdgeReads[VR, PersistedRecordProperty]()(implicitly[Reads[PersistedVertex#Id]], PersistedRecordPropertyFormat)

}
