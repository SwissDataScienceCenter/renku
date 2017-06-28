package ch.datascience.graph.elements.mutation.json

import ch.datascience.graph.elements.mutation.create.CreateVertexOperation
import ch.datascience.graph.elements.new_.json.NewVertexFormat
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object CreateVertexOperationFormat extends Format[CreateVertexOperation] {

  def writes(op: CreateVertexOperation): JsValue = writer.writes(op)

  def reads(json: JsValue): JsResult[CreateVertexOperation] = reader.reads(json)

  private[this] lazy val writer: Writes[CreateVertexOperation] = (
    (JsPath \ "type").write[String] and
      (JsPath \ "element").write[CreateVertexOperation#ElementType]
  ) { op => ("create_vertex", op.vertex) }

  private[this] lazy val reader: Reads[CreateVertexOperation] = (
    (JsPath \ "type").read[String].filter(typeError)(_ == "create_vertex") and
      (JsPath \ "element").read[CreateVertexOperation#ElementType]
  ) { (_, vertex) => CreateVertexOperation(vertex) }

  private[this] lazy val typeError = ValidationError("expected type: 'create_vertex'")

  private[this] implicit lazy val newVertexFormat: Format[CreateVertexOperation#ElementType] = NewVertexFormat

}
