package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.{Property, RichProperty}
import ch.datascience.graph.elements.mutation.CreateOperation
import ch.datascience.graph.elements.persistence.NewVertex
/**
  * Created by jeberle on 10.05.17.
  */
case class ImplCreateVertexOperation[TypeId, Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp], +Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]](
  vertex: NewVertex[TypeId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]
) extends CreateOperation[Nothing, Nothing, NewVertex[TypeId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]]

/*
case class ImplCreateVertexPropertyOperation[+TypeId, Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp], +Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]](
  parent: Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  vertexProperty: NewVertexProperty[Key, Value, MetaKey, MetaValue,MetaProp, Prop]
) extends CreateOperation[TypeId, Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop], NewVertexProperty[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]]

case class ImplCreateEdgeOperation[TypeId, Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp], +Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]](
  edge: NewEdge[TypeId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]
) extends CreateOperation[Nothing, Nothing, NewEdge[TypeId, Key, Value, MetaKey, MetaValue,MetaProp, Prop]]
*/