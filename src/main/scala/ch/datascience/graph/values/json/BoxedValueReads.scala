package ch.datascience.graph.values.json

import ch.datascience.graph.types.DataType
import ch.datascience.graph.types.json.DataTypeReads
import ch.datascience.graph.values.BoxedValue
import play.api.libs.json._

/**
  * Created by johann on 24/05/17.
  */
object BoxedValueReads extends Reads[BoxedValue] {

//  def reads(json: JsValue): JsResult[BoxedValue] = {
//    val result = for {
//      dataType <- dataTypeReads.reads(json)
//      value <- valueReads(dataType).reads(json)
//    } yield value
//
//    // Need to repath result
//    result match {
//      case JsSuccess(x, _) => JsSuccess(x)
//      case _ => result
//    }
//  }

  def reads(json: JsValue): JsResult[BoxedValue] = self.reads(json)

  private[this] lazy val self: Reads[BoxedValue] = dataTypeReads.flatMap(valueReads)

  private[this] lazy val dataTypeReads: Reads[DataType] = (JsPath \ "data_type").read[DataType](DataTypeReads)

  private[this] def valueReads(dataType: DataType): Reads[BoxedValue] = (JsPath \ "value").read[BoxedValue](ValueReads(dataType))

}
