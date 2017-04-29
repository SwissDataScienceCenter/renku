package ch.datascience.graph.elements

import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
abstract class VertexProperty[
    Key,
    Value : ValidValue,
    MetaKey,
    MetaProp[K, V] <: Property[K, V, MetaProp],
    This[K, V, MK] <: VertexProperty[K, V, MK, MetaProp, This]
//    This[K, V, MK, MP[MPK, MPV] <: Property[MPK, MPV, MP]] <: VertexProperty[K, V, MK, MP, This]
] extends Property[Key, Value, VertexPropertyHelper[MetaKey, MetaProp, This]#VertexPropertyKV]
//] extends Property[Key, Value, VertexPropertyHelper[MetaKey, MetaProp, This]#VertexPropertyKV]
  with HasProperties[MetaKey, BoxedValue, MetaProp] { this: This[Key, Value, MetaKey] =>

  override final def validPropertyValuesEvidence: ValidValue[BoxedValue] = implicitly[ValidValue[BoxedValue]]

  val properties: Map[MetaKey, MetaProp[MetaKey, BoxedValue]]

//  type VertexPropertyKV[K, V] = This[K, V, MetaKey]

}

//private[this] class VertexPropertyHelper[
//    MetaKey,
//    MetaProp[K, V] <: Property[K, V, MetaProp],
//    This[K, V, MK, MP[MPK, MPV] <: Property[MPK, MPV, MP]] <: VertexProperty[K, V, MK, MP, This]
//] {
//  type VertexPropertyKV[K, V] = This[K, V, MetaKey, MetaProp]
//}

private[this] class VertexPropertyHelper[
    MetaKey,
    MetaProp[K, V] <: Property[K, V, MetaProp],
    This[K, V, MK] <: VertexProperty[K, V, MK, MetaProp, This]
] {
  type VertexPropertyKV[K, V] = This[K, V, MetaKey]
}

