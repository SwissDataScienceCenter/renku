package ch.datascience.graph.elements

import ch.datascience.graph.bases.HasTypes
import ch.datascience.graph.Constants

trait TypedRecord extends Record with HasTypes {

  final type TypeId = Constants.TypeId

  //  protected[elements] def <|(graphType: GraphType { type TypeId = self.TypeId; type Key = Prop#Key }): Boolean = graphType match {
  //    case NamedType(tid, _, _) => types contains tid
  //    case _                    => super.<|(graphType)
  //  }
}

///**
//  * Base trait for records that have properties which are constrained by types
//  *
//  * Typed records can be validated (see package types).
//  *
//  */
//trait TypedRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value]]
//  extends Record[Key, Value, Prop]
//    with HasTypes[TypeId] {
//
//  protected[elements] override def <|(graphType: GraphType): Boolean = graphType match {
//    case nrt: NamedType[TypeId, Key] => types contains nrt.key
//    case _ => super.<|(graphType)
//  }
//
//}
