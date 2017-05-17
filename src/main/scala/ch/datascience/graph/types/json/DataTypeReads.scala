package ch.datascience.graph.types.json

import ch.datascience.graph.types.DataType
import play.api.libs.json._

/**
  * Created by johann on 26/04/17.
  */
object DataTypeReads extends Reads[DataType] {

  def reads(json: JsValue): JsResult[DataType] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(DataType(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
