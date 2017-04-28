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
    VertexPropertyType[K, V, MK, MP[MPK, MPV] <: Property[MPK, MPV, MP]] <: VertexProperty[K, V, MK, MP, VertexPropertyType]
 ] extends TypedElement[TypeId, Key, BoxedValue, VertexPropertyHelper[MetaKey, MetaProp, VertexPropertyType]#VertexPropertyKV] {

  override final def validMultiPropertyValuesEvidence: ValidValue[BoxedValue] = implicitly[ValidValue[BoxedValue]]

//  type VertexPropertyTypeV[V] = VertexPropertyType[Key, V, MetaKey, MetaProp]
//  type MultiPropertiesType = MultiProperties[Key, BoxedValue, VertexPropertyTypeV]
//  val properties: MultiPropertiesType

}
