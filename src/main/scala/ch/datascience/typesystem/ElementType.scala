package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
sealed abstract class ElementType

case object VertexType extends ElementType

case object EdgeType extends ElementType
