package ch.datascience.graph.types.json

import ch.datascience.graph.types.DataType
import play.api.libs.json.{JsString, Writes}

/**
  * Created by johann on 17/05/17.
  */
object DataTypeWrites extends Writes[DataType] {

  def writes(dataType: DataType): JsString = JsString(dataType.name)

}
