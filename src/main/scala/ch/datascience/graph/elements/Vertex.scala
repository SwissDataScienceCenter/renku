package ch.datascience.graph.elements

import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
abstract class Vertex[
    TypeId,
    Key,
    MetaKey,
    MetaProp[K, V] <: Property[K, V, MetaProp],
    VertexPropertyType[K, V, MK] <: VertexProperty[K, V, MK, MetaProp, VertexPropertyType]
 ] extends TypedElement[TypeId, Key, BoxedValue, VertexPropertyHelper[MetaKey, MetaProp, VertexPropertyType]#VertexPropertyKV] {

  override final def validMultiPropertyValuesEvidence: ValidValue[BoxedValue] = implicitly[ValidValue[BoxedValue]]

}
