package ch.datascience.graph.elements.mutation.json

import ch.datascience.graph.elements.mutation.Operation
import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation}
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object OperationFormat extends Format[Operation] {

  def writes(op: Operation): JsValue = op match {
    case o: CreateVertexOperation => CreateVertexOperationFormat.writes(o)
    case o: CreateEdgeOperation => CreateEdgeOperationFormat.writes(o)

    case _ => throw new IllegalArgumentException(s"Unsupported operation: $op")
  }

  def reads(json: JsValue): JsResult[Operation] = reader.reads(json)


  private[this] lazy val reader: Reads[Operation] = (JsPath \ "type").read[String].flatMap {
    case "create_vertex" => CreateVertexOperationFormat.map { op => op: Operation }
    case "create_edge" => CreateEdgeOperationFormat.map { op => op: Operation }

    case t => throw new IllegalArgumentException(s"Unsupported operation type: $t")
  }

}
