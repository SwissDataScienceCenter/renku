package ch.datascience.graph.elements.mutation.json

import ch.datascience.graph.elements.mutation.{Mutation, Operation}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 08/06/17.
  */
object MutationFormat extends Format[Mutation] {

  def writes(mutation: Mutation): JsValue = writer.writes(mutation)

  def reads(json: JsValue): JsResult[Mutation] = reader.reads(json)

  private[this] lazy val writer: Writes[Mutation] = Writes { mutation =>
    (JsPath \ "operations").write[Seq[Operation]].writes(mutation.operations)
  }

  private[this] lazy val reader: Reads[Mutation] = Reads { json =>
    (JsPath \ "operations").read[Seq[Operation]].reads(json).flatMap { seq =>
      try {
        JsSuccess(Mutation(seq))
      } catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }
  }

  private[this] implicit lazy val operationFormat: Format[Operation] = OperationFormat

}
