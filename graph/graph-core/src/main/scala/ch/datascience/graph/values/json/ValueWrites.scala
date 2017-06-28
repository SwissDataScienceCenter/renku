package ch.datascience.graph.values.json

import java.util.UUID

import ch.datascience.graph.values._
import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 24/05/17.
  */
object ValueWrites extends Writes[BoxedValue] {

  def writes(value: BoxedValue): JsValue = value match {
    case StringValue(v)  => implicitly[Writes[String]].writes(v)
    case CharValue(v)    => implicitly[Writes[String]].writes(v.toString)
    case BooleanValue(v) => implicitly[Writes[Boolean]].writes(v)
    case ByteValue(v)    => implicitly[Writes[Byte]].writes(v)
    case ShortValue(v)   => implicitly[Writes[Short]].writes(v)
    case IntValue(v)     => implicitly[Writes[Int]].writes(v)
    case LongValue(v)    => implicitly[Writes[Long]].writes(v)
    case FloatValue(v)   => implicitly[Writes[Float]].writes(v)
    case DoubleValue(v)  => implicitly[Writes[Double]].writes(v)
    case UuidValue(v)    => implicitly[Writes[UUID]].writes(v)
  }

}
