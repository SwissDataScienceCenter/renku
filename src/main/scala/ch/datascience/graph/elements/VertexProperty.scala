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
    This[K, V, MK, MP[MPK, MPV] <: Property[MPK, MPV, MP]] <: VertexProperty[K, V, MK, MP, This]
] extends Property[Key, Value, VertexPropertyHelper[MetaKey, MetaProp, This]#VertexPropertyKV]
  with HasProperties[MetaKey, BoxedValue, MetaProp] {

  override final def validPropertyValuesEvidence: ValidValue[BoxedValue] = implicitly[ValidValue[BoxedValue]]

  val properties: Map[MetaKey, MetaProp[MetaKey, BoxedValue]]

}

private[this] class VertexPropertyHelper[
    MetaKey,
    MetaProp[K, V] <: Property[K, V, MetaProp],
    This[K, V, MK, MP[MPK, MPV] <: Property[MPK, MPV, MP]] <: VertexProperty[K, V, MK, MP, This]
] {
  type VertexPropertyKV[K, V] = This[K, V, MetaKey, MetaProp]
}
