package ch.datascience.graph.elements

trait Vertex extends TypedMultiRecord {

  type Prop <: RichProperty

}

///**
//  * Created by johann on 27/04/17.
//  */
//trait Vertex[
//TypeId,
//Key,
//+Value,
//+MetaValue,
//+MetaProp <: Property[Key, MetaValue],
//+Prop <: RichProperty[Key, Value, MetaValue, MetaProp]
//] extends TypedMultiRecord[TypeId, Key, Value, Prop]
