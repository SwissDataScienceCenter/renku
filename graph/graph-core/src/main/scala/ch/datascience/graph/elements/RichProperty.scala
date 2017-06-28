package ch.datascience.graph.elements

trait RichProperty extends Property with Record with Element

///**
//  *
//  * @tparam Key       key type
//  * @tparam Value     value type
//  * @tparam MetaValue meta-value type
//  * @tparam MetaProp  meta-property type
//  */
//trait RichProperty[Key, +Value, +MetaValue, +MetaProp <: Property[Key, MetaValue]]
//  extends Property[Key, Value]
//    with Record[Key, MetaValue, MetaProp]
//    with Element
