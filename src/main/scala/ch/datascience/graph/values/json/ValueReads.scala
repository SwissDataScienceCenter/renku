package ch.datascience.graph.values.json

import java.util.UUID

import ch.datascience.graph.types.DataType
import ch.datascience.graph.values.BoxedValue
import play.api.libs.json.{JsResult, JsValue, Reads}


/**
  * Created by johann on 24/05/17.
  */
case class ValueReads(dataType: DataType) extends Reads[BoxedValue] {

  def reads(json: JsValue): JsResult[BoxedValue] = dataType match {
    case DataType.String    => implicitly[Reads[String]].reads(json) map BoxedValue.apply
    case DataType.Character => implicitly[Reads[String]].reads(json) map { str => BoxedValue.apply(str.head) }
    case DataType.Boolean   => implicitly[Reads[Boolean]].reads(json) map BoxedValue.apply
    case DataType.Byte      => implicitly[Reads[Byte]].reads(json) map BoxedValue.apply
    case DataType.Short     => implicitly[Reads[Short]].reads(json) map BoxedValue.apply
    case DataType.Integer   => implicitly[Reads[Int]].reads(json) map BoxedValue.apply
    case DataType.Long      => implicitly[Reads[Long]].reads(json) map BoxedValue.apply
    case DataType.Float     => implicitly[Reads[Float]].reads(json) map BoxedValue.apply
    case DataType.Double    => implicitly[Reads[Double]].reads(json) map BoxedValue.apply
    case DataType.UUID      => implicitly[Reads[UUID]].reads(json) map BoxedValue.apply
  }

}
