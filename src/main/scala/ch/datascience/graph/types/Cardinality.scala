package ch.datascience.graph.types

/**
  * Created by johann on 09/04/17.
  */
sealed abstract class Cardinality(val name: String)

object Cardinality {

  def apply(name: String): Cardinality = name.toLowerCase match {
    case Single.name => Single
    case List.name => List
    case Set.name => Set
  }

  case object Single extends Cardinality(name = "single")

  case object List extends Cardinality(name = "list")

  case object Set extends Cardinality(name = "set")

  def valueOf(name: String): Cardinality = Cardinality.apply(name)

}
