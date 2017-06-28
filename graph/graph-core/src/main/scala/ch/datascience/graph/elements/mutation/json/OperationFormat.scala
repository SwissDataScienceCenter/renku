package ch.datascience.graph.elements.mutation.json

import ch.datascience.graph.elements.mutation.Operation
import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation, CreateVertexPropertyOperation}
import ch.datascience.graph.elements.mutation.update.UpdateVertexPropertyOperation
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object OperationFormat extends Format[Operation] {

  def writes(op: Operation): JsValue = op match {
    case o: CreateVertexOperation => CreateVertexOperationFormat.writes(o)
    case o: CreateEdgeOperation => CreateEdgeOperationFormat.writes(o)
    case o: CreateVertexPropertyOperation => CreateVertexPropertyOperationFormat.writes(o)
    case o: UpdateVertexPropertyOperation => UpdateVertexPropertyOperationFormat.writes(o)

    case _ => unsupportedOperationFormat.writes(op)
  }

  def reads(json: JsValue): JsResult[Operation] = reader.reads(json)


  private[this] lazy val reader: Reads[Operation] = (JsPath \ "type").read[String].flatMap {
    case "create_vertex" => CreateVertexOperationFormat.map { op => op: Operation }
    case "create_edge" => CreateEdgeOperationFormat.map { op => op: Operation }
    case "create_vertex_property" => CreateVertexPropertyOperationFormat.map { op => op: Operation }
    case "update_vertex_property" => UpdateVertexPropertyOperationFormat.map { op => op: Operation }

    case t => unsupportedOperationFormat
  }

  private[this] lazy val unsupportedOperationFormat: Format[Operation] = new Format[Operation] {

    def writes(op: Operation): JsValue = throw new IllegalArgumentException(s"Unsupported operation: $op")

    def reads(json: JsValue): JsResult[Operation] = JsError(s"Unsupported operation type: ${(json \ "type").as[String]}")

  }

}
