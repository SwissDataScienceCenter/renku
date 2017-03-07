package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
sealed trait Constraint

sealed trait UniqueConstraint extends Constraint

case class PropertyKeyConstraint(name: String,
                                 elementType: ElementType,
                                 keys: Set[String]) extends UniqueConstraint
