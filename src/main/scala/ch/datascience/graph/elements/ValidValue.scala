package ch.datascience.graph.elements

import ch.datascience.graph.types.DataType

/**
  * Created by johann on 27/04/17.
  */
sealed trait ValidValue[V] {

  def dataType(value: V): DataType

  def boxed(value: V): BoxedValue

}

object ValidValue {

  // Valid value types
  implicit object StringIsValid extends ValidValue[String] {
    def dataType(value: String) = DataType.String
    def boxed(value: String) = BoxedValue(value)
  }

  implicit object CharIsValid extends ValidValue[Char] {
    def dataType(value: Char) = DataType.Character
    def boxed(value: Char) = BoxedValue(value)
  }

  implicit object BooleanIsValid extends ValidValue[Boolean] {
    def dataType(value: Boolean) = DataType.Boolean
    def boxed(value: Boolean) = BoxedValue(value)
  }

  implicit object ByteIsValid extends ValidValue[Byte] {
    def dataType(value: Byte) = DataType.Byte
    def boxed(value: Byte) = BoxedValue(value)
  }

  implicit object ShortIsValid extends ValidValue[Short] {
    def dataType(value: Short) = DataType.Short
    def boxed(value: Short) = BoxedValue(value)
  }

  implicit object IntIsValid extends ValidValue[Int] {
    def dataType(value: Int) = DataType.Integer
    def boxed(value: Int) = BoxedValue(value)
  }

  implicit object LongIsValid extends ValidValue[Long] {
    def dataType(value: Long) = DataType.Long
    def boxed(value: Long) = BoxedValue(value)
  }

  implicit object FloatIsValid extends ValidValue[Float] {
    def dataType(value: Float) = DataType.Float
    def boxed(value: Float) = BoxedValue(value)
  }

  implicit object DoubleIsValid extends ValidValue[Double] {
    def dataType(value: Double) = DataType.Double
    def boxed(value: Double) = BoxedValue(value)
  }

  implicit object BoxedIsValid extends ValidValue[BoxedValue] {
    def dataType(value: BoxedValue): DataType = value.dataType
    def boxed(value: BoxedValue) = value
  }

}
