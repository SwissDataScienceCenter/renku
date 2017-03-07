package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
sealed abstract class Cardinality

object Cardinality {
  case object Single extends Cardinality
  case object List extends Cardinality
  case object Set extends Cardinality
}
