package ch.datascience.graph.elements.persisted

import ch.datascience.graph.Constants
import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements.Vertex
import ch.datascience.graph.elements.persisted.impl.ImplPersistedVertex

/**
  * Created by johann on 29/05/17.
  */
trait PersistedVertex extends Vertex with PersistedElement with HasId {

  final type Id = Constants.VertexId

  final type PathType = VertexPath

  final type Prop = PersistedVertexProperty

  final def path: VertexPath = VertexPath(id)

}

object PersistedVertex {

  def apply(
    id: PersistedVertex#Id,
    types: Set[PersistedVertex#TypeId],
    properties: PersistedVertex#Properties
  ): PersistedVertex = ImplPersistedVertex(id, types, properties)

  def unapply(vertex: PersistedVertex): Option[(PersistedVertex#Id, Set[PersistedVertex#TypeId], PersistedVertex#Properties)] = {
    if (vertex eq null)
      None
    else
      Some(vertex.id, vertex.types, vertex.properties)
  }

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
