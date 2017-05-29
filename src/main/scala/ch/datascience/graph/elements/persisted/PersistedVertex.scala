package ch.datascience.graph.elements.persisted

import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements.Vertex
import ch.datascience.graph.Constants

/**
  * Created by johann on 29/05/17.
  */
trait PersistedVertex extends Vertex with PersistedElement with HasId {

  final type Id = Constants.VertexId

  final type PathType = VertexPath[Id]

  final type Prop = PersistedMultiRecordRichProperty

  final def path: VertexPath[Id] = VertexPath(id)

}

//trait PersistedVertex[
//+Id,
//TypeId,
//Key,
//+Value,
//+MetaValue,
//+MetaProp <: PersistedRecordProperty[Key, MetaValue],
//+PropId,
//+Prop <: PersistedMultiRecordRichProperty[PropId, Key, Value, MetaValue, MetaProp]
//] extends Vertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]
//  with PersistedElement[VertexPath[Id]]
//  with HasId[Id] {
//
//  final def path: VertexPath[Id] = VertexPath(id)
//
//}
