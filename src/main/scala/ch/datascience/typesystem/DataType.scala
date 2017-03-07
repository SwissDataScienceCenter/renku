package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
sealed abstract class DataType

object DataType {

  case object String extends DataType
  case object Character extends DataType
  case object Boolean extends DataType
  case object Byte extends DataType
  case object Short extends DataType
  case object Integer extends DataType
  case object Long extends DataType
  case object Float extends DataType
  case object Double extends DataType
  case object Decimal extends DataType
  case object Precision extends DataType
  case object Date extends DataType
  case object Geoshape extends DataType
  case object UUID extends DataType

}
