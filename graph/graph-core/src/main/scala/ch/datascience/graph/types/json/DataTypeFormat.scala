package ch.datascience.graph.types.json

import ch.datascience.graph.types.DataType
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object DataTypeFormat extends Format[DataType] {

  def writes(dataType: DataType): JsString = JsString(dataType.name)

  def reads(json: JsValue): JsResult[DataType] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(DataType(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
