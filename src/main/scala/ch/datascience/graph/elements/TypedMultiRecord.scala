package ch.datascience.graph.elements

import ch.datascience.graph.Constants
import ch.datascience.graph.bases.HasTypes

trait TypedMultiRecord extends MultiRecord with HasTypes {

  final type TypeId = Constants.TypeId

}

///**
//  * Base trait for multi-records that have multi-properties which are constrained by types
//  *
//  * Typed  multi-records can be validated (see package types).
//  *
//  */
//trait TypedMultiRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value]]
//  extends MultiRecord[Key, Value, Prop]
//    with HasTypes[TypeId]
