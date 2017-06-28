package ch.datascience.graph.elements

trait MultiRecord extends Element {

  type Prop <: Property
  final type Properties = Map[Prop#Key, MultiPropertyValue[Prop]]

  /**
    * Properties
    * @return the properties
    */
  def properties: Properties

  //TODO: define <|, like for [[Record]]

}

///**
//  * Base trait for multi-records, i.e. elements that hold multi-properties (single, set, or list cardinality)
//  *
//  * Properties can be validated (see package types).
//  *
//  */
//trait MultiRecord[Key, +Value, +Prop <: Property[Key, Value]] extends Element {
//
//  def properties: MultiProperties[Key, Value, Prop]
//
//  //TODO: define <|, like for [[Record]]
//
//}
