package ch.datascience.graph.elements.new_.json

import ch.datascience.graph.elements.new_.NewEdge
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object VertexReferenceReads extends Reads[NewEdge#VertexReference] {

  def reads(json: JsValue): JsResult[Either[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id]] = self.reads(json)

  private[this] lazy val self: Reads[Either[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id]] = typeReads.flatMap {
    case Left(()) => (
      (JsPath \ "type").read[String] and
        (JsPath \ "id").read[NewEdge#NewVertexType#TempId]
      ) { (_, tempId) => Left(tempId) }
    case Right(()) => (
      (JsPath \ "type").read[String] and
        (JsPath \ "id").read[NewEdge#PersistedVertexType#Id]
      ) { (_, id) => Right(id) }
  }

  private[this] lazy val typeReads: Reads[Either[Unit, Unit]] = Reads { json =>
    (JsPath \ "type").read[String].reads(json).flatMap {
      case "new_vertex" => JsSuccess(Left(()))
      case "persisted_vertex" => JsSuccess(Right(()))
      case _ => JsError("type must be 'new_vertex' or 'persisted_vertex'")
    }
  }

}
