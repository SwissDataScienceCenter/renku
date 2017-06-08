package ch.datascience.graph.elements.new_.json

import ch.datascience.graph.elements.new_.NewEdge
import play.api.libs.json.{JsPath, JsValue, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by johann on 08/06/17.
  */
object VertexReferenceWrites extends Writes[NewEdge#VertexReference] {

  def writes(ref: Either[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id]): JsValue = ref match {
    case Left(tempId) => leftWrites.writes(tempId)
    case Right(id) => rightWrites.writes(id)
  }

  private[this] lazy val leftWrites: Writes[NewEdge#NewVertexType#TempId] = (
    (JsPath \ "type").write[String] and
      (JsPath \ "id").write[NewEdge#NewVertexType#TempId]
    ) { tempId => ("new_vertex", tempId) }

  private[this] lazy val rightWrites: Writes[NewEdge#PersistedVertexType#Id] = (
    (JsPath \ "type").write[String] and
      (JsPath \ "id").write[NewEdge#PersistedVertexType#Id]
    ) { id => ("persisted_vertex", id) }

}
