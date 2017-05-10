package models
package json

import ch.datascience.graph.types.DataType
import play.api.libs.json._

/**
  * Created by johann on 26/04/17.
  */
object DataTypeMappers {

  def dataTypeWrites: Writes[DataType] = new Writes[DataType] {
    def writes(dataType: DataType): JsString = JsString(dataType.name)
  }

  def dataTypeReads: Reads[DataType] = new Reads[DataType] {
    def reads(json: JsValue): JsResult[DataType] = json.validate[String] flatMap { str =>
      try {
        JsSuccess(DataType(str))
      } catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }
  }

}
