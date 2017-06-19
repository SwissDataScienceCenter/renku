package ch.datascience.graph.elements.mutation.json

import ch.datascience.graph.elements.mutation.create.CreateVertexPropertyOperation
import ch.datascience.graph.elements.new_.json.NewRichPropertyFormat
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object CreateVertexPropertyOperationFormat extends Format[CreateVertexPropertyOperation] {

  def writes(op: CreateVertexPropertyOperation): JsValue = writer.writes(op)

  def reads(json: JsValue): JsResult[CreateVertexPropertyOperation] = reader.reads(json)

  private[this] lazy val writer: Writes[CreateVertexPropertyOperation] = (
    (JsPath \ "type").write[String] and
      (JsPath \ "element").write[CreateVertexPropertyOperation#ElementType]
  ) { op => ("create_vertex_property", op.vertexProperty) }

  private[this] lazy val reader: Reads[CreateVertexPropertyOperation] = (
    (JsPath \ "type").read[String].filter(typeError)(_ == "create_vertex_property") and
      (JsPath \ "element").read[CreateVertexPropertyOperation#ElementType]
  ) { (_, vertexProperty) => CreateVertexPropertyOperation(vertexProperty) }

  private[this] lazy val typeError = ValidationError("expected type: 'create_vertex_property'")

  private[this] implicit lazy val newVertexFormat: Format[CreateVertexPropertyOperation#ElementType] = NewRichPropertyFormat

}
