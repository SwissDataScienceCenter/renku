package ch.datascience.graph.elements.mutation.json

import ch.datascience.graph.elements.mutation.create.CreateEdgeOperation
import ch.datascience.graph.elements.new_.json.NewEdgeFormat
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 08/06/17.
  */
object CreateEdgeOperationFormat extends Format[CreateEdgeOperation] {

  def writes(op: CreateEdgeOperation): JsValue = writer.writes(op)

  def reads(json: JsValue): JsResult[CreateEdgeOperation] = reader.reads(json)

  private[this] lazy val writer: Writes[CreateEdgeOperation] = (
    (JsPath \ "type").write[String] and
      (JsPath \ "element").write[CreateEdgeOperation#ElementType]
  ) { op => ("create_edge", op.edge) }

  private[this] lazy val reader: Reads[CreateEdgeOperation] = (
    (JsPath \ "type").read[String].filter(typeError)(_ == "create_edge") and
      (JsPath \ "element").read[CreateEdgeOperation#ElementType]
  ) { (_, edge) => CreateEdgeOperation(edge) }

  private[this] lazy val typeError = ValidationError("expected type: 'create_edge'")

  private[this] implicit lazy val newEdgeFormat: Format[CreateEdgeOperation#ElementType] = NewEdgeFormat

}
