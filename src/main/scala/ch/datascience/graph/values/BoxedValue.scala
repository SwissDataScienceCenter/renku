package ch.datascience.graph.values

import java.util.UUID

import ch.datascience.graph.types.DataType

/**
  * Created by johann on 27/04/17.
  */
sealed abstract class BoxedValue {

  type Self
  val self: Self

  def isValidValue: ValidValue[Self]

  final def dataType: DataType = this match {
    case _: StringValue  => DataType.String
    case _: CharValue    => DataType.Character
    case _: BooleanValue => DataType.Boolean
    case _: ByteValue    => DataType.Byte
    case _: ShortValue   => DataType.Short
    case _: IntValue     => DataType.Integer
    case _: LongValue    => DataType.Long
    case _: FloatValue   => DataType.Float
    case _: DoubleValue  => DataType.Double
    case _: UuidValue    => DataType.UUID
  }

  @throws[java.lang.ClassCastException]
  def unboxAs[V: ValidValue]: V = self.asInstanceOf[V]

}

final case class StringValue(self: String) extends BoxedValue {
  type Self = String

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class CharValue(self: Char) extends BoxedValue {
  type Self = Char

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class BooleanValue(self: Boolean) extends BoxedValue {
  type Self = Boolean

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class ByteValue(self: Byte) extends BoxedValue {
  type Self = Byte

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class ShortValue(self: Short) extends BoxedValue {
  type Self = Short

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class IntValue(self: Int) extends BoxedValue {
  type Self = Int

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class LongValue(self: Long) extends BoxedValue {
  type Self = Long

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class FloatValue(self: Float) extends BoxedValue {
  type Self = Float

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class DoubleValue(self: Double) extends BoxedValue {
  type Self = Double

  def isValidValue: ValidValue[Self] = implicitly[ValidValue[Self]]
}

final case class UuidValue(self: UUID) extends BoxedValue {
  type Self = UUID

  def isValidValue: ValidValue[UUID] = implicitly[ValidValue[Self]]
}

object BoxedValue {

  def apply(x: String): BoxedValue = StringValue(x)

  def apply(x: Char): BoxedValue = CharValue(x)

  def apply(x: Boolean): BoxedValue = BooleanValue(x)

  def apply(x: Byte): BoxedValue = ByteValue(x)

  def apply(x: Short): BoxedValue = ShortValue(x)

  def apply(x: Int): BoxedValue = IntValue(x)

  def apply(x: Long): BoxedValue = LongValue(x)

  def apply(x: Float): BoxedValue = FloatValue(x)

  def apply(x: Double): BoxedValue = DoubleValue(x)

  def apply(x: UUID): BoxedValue = UuidValue(x)

  implicit object boxedIsBoxed extends IsBoxedValue[BoxedValue] {
    def asBoxedValue(v: BoxedValue): BoxedValue = v
  }

}

sealed trait IsBoxedValue[V] {
  def asBoxedValue(v: V): BoxedValue
}
