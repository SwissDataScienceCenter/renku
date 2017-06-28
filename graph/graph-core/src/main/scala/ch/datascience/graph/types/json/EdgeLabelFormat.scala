package ch.datascience.graph.types.json

import ch.datascience.graph.types.{Multiplicity, EdgeLabel}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object EdgeLabelFormat extends Format[EdgeLabel] {

  def writes(edgeLabel: EdgeLabel): JsValue = writer.writes(edgeLabel)

  def reads(json: JsValue): JsResult[EdgeLabel] = reader.reads(json)

  private[this] def writer: Writes[EdgeLabel] = (
    (JsPath \ "key").write[EdgeLabel#Key](propKeyFormat) and
      (JsPath \ "multiplicity").write[Multiplicity](MultiplicityFormat)
    )(unlift(EdgeLabel.unapply))

  private[this] def reader: Reads[EdgeLabel] = (
    (JsPath \ "key").read[EdgeLabel#Key](propKeyFormat) and
      (JsPath \ "multiplicity").read[Multiplicity](MultiplicityFormat)
    )(EdgeLabel.apply _)
}
