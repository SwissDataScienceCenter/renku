package ch.datascience.graph.elements

import ch.datascience.graph.bases.HasTypes
import ch.datascience.graph.types.{GraphType, NamedType}

/**
  * Base trait for records that have properties which are constrained by types
  *
  * Typed records can be validated (see package types).
  *
  */
trait TypedRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value]]
  extends Record[Key, Value, Prop]
    with HasTypes[TypeId] {

  protected[elements] override def <|(graphType: GraphType): Boolean = graphType match {
    case nrt: NamedType[TypeId, Key] => types contains nrt.key
    case _ => super.<|(graphType)
  }

}
