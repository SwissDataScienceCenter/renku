package ch.datascience.graph.elements.mappers.tinkerpop

import ch.datascience.graph.values._

/**
  * Created by johann on 24/05/17.
  */
object BoxedWriter extends Writer[BoxedValue, java.lang.Object] {

  def write(value: BoxedValue): java.lang.Object = value match {
    case StringValue(self)  => self
    case BooleanValue(self) => Boolean.box(self)
    case ByteValue(self)    => Byte.box(self)
    case CharValue(self)    => Char.box(self)
    case DoubleValue(self)  => Double.box(self)
    case FloatValue(self)   => Float.box(self)
    case IntValue(self)     => Int.box(self)
    case LongValue(self)    => Long.box(self)
    case ShortValue(self)   => Short.box(self)
    case UuidValue(self)    => self
  }

}
