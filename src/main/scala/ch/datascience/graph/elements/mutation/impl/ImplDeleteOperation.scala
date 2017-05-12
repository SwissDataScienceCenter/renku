package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.mutation.DeleteOperation
import ch.datascience.graph.elements.persistence.{PersistedMultiRecordProperty, PersistedRecordProperty, PersistedVertex, VertexPath}

/**
  * Created by jeberle on 10.05.17.
  */

case class ImplDeleteVertexOperation[PersistedId, TypeId, Key, +Value, MetaKey, +MetaValue, +MetaProp <: PersistedRecordProperty[MetaKey, MetaValue, MetaProp], PropId,+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop] with PersistedMultiRecordProperty[PropId, Key, Value, Prop]](
  vertex: PersistedVertex[PersistedId, TypeId, Key, Value, MetaKey, MetaValue, MetaProp, PropId, Prop]
) extends DeleteOperation[VertexPath[PersistedId], PersistedVertex[PersistedId, TypeId, Key, Value, MetaKey, MetaValue, MetaProp, PropId, Prop]]

/*
case class ImplDeleteVertexPropertyOperation[PersistedId, +TypeId, Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp], +Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]](
  parent: Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  vertexProperty: PersistedVertexProperty[Key, Value, MetaKey, MetaValue,MetaProp, Prop]
) extends DeleteOperation[TypeId, Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop], PersistedId, PersistedVertexProperty[TemporaryId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]]

case class ImplDeleteEdgeOperation[PersistedId, Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp], +Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]](
  edge: PersistedEdge[PersistedId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]
) extends DeleteOperation[PersistedId, PersistedEdge[PersistedId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]]
*/
