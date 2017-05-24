package ch.datascience.graph.types

import java.util.UUID

/**
  * Created by johann on 09/04/17.
  */
sealed abstract class DataType(val name: String) {

  type ScalaType

  def javaClass(): Class[ScalaType]

}

object DataType {

  val dataTypes: Seq[DataType] = Seq(String, Character, Boolean, Byte, Short, Integer, Long, Float, Double, UUID)

  def valueOf(name: String): DataType = DataType.apply(name)

  def apply(name: String): DataType = name.toLowerCase match {
    case String.name    => String
    case Character.name => Character
    case Boolean.name   => Boolean
    case Byte.name      => Byte
    case Short.name     => Short
    case Integer.name   => Integer
    case Long.name      => Long
    case Float.name     => Float
    case Double.name    => Double
    case UUID.name      => UUID
  }

  def getDataType(x: Any): DataType = x match {
    case _: String  => DataType.String
    case _: Char    => DataType.Character
    case _: Boolean => DataType.Boolean
    case _: Byte    => DataType.Byte
    case _: Short   => DataType.Short
    case _: Int     => DataType.Integer
    case _: Long    => DataType.Long
    case _: Float   => DataType.Float
    case _: Double  => DataType.Double
    case _: UUID    => DataType.UUID
    case _          => throw new IllegalArgumentException(s"Data type unknown for: $x")
  }

  case object String extends DataType(name = "string") {
    override type ScalaType = java.lang.String

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Character extends DataType(name = "character") {
    override type ScalaType = java.lang.Character

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Boolean extends DataType(name = "boolean") {
    override type ScalaType = java.lang.Boolean

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Byte extends DataType(name = "byte") {
    override type ScalaType = java.lang.Byte

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Short extends DataType(name = "short") {
    override type ScalaType = java.lang.Short

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Integer extends DataType(name = "integer") {
    override type ScalaType = java.lang.Integer

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Long extends DataType(name = "long") {
    override type ScalaType = java.lang.Long

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Float extends DataType(name = "float") {
    override type ScalaType = java.lang.Float

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object Double extends DataType(name = "double") {
    override type ScalaType = java.lang.Double

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

  case object UUID extends DataType(name = "uuid") {
    override type ScalaType = java.util.UUID

    override def javaClass(): Class[ScalaType] = classOf[ScalaType]
  }

}
