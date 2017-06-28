package ch.datascience.graph.values.json

import ch.datascience.graph.types.DataType
import ch.datascience.graph.types.json.DataTypeFormat
import ch.datascience.graph.values.BoxedValue
import play.api.libs.json._

/**
  * Created by johann on 24/05/17.
  */
object BoxedValueReads extends Reads[BoxedValue] {

  def reads(json: JsValue): JsResult[BoxedValue] = self.reads(json)

  private[this] lazy val self: Reads[BoxedValue] = dataTypeReads.flatMap(valueReads)

  private[this] lazy val dataTypeReads: Reads[DataType] = (JsPath \ "data_type").read[DataType](DataTypeFormat)

  private[this] def valueReads(dataType: DataType): Reads[BoxedValue] = (JsPath \ "value").read[BoxedValue](ValueReads(dataType))

}
