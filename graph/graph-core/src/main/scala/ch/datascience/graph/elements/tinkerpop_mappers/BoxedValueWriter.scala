package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.values._

/**
  * Created by johann on 30/05/17.
  */
case object BoxedValueWriter extends Writer[BoxedValue, java.lang.Object] {

  def write(value: BoxedValue): java.lang.Object = value match {
    case StringValue(str) => str
    case CharValue(x) => Char.box(x)
    case BooleanValue (x) => Boolean.box(x)
    case ByteValue    (x) => Byte.box(x)
    case ShortValue   (x) => Short.box(x)
    case IntValue     (x) => Int.box(x)
    case LongValue    (x) => Long.box(x)
    case FloatValue   (x) => Float.box(x)
    case DoubleValue  (x) => Double.box(x)
    case UuidValue    (uuid) => uuid
  }

}
