package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.{Property, RichProperty}
import ch.datascience.graph.elements.mutation.CreateOperation
import ch.datascience.graph.elements.persistence.NewVertex
/**
  * Created by jeberle on 10.05.17.
  */
case class ImplCreateVertexOperation[
TypeId,
Key,
+Value,
+MetaValue,
+MetaProp <: Property[Key, MetaValue],
+Prop <: RichProperty[Key, Value, MetaValue, MetaProp]
](
  vertex: NewVertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]
) extends CreateOperation[NewVertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]]

case class ImplCreateEdgeOperation[+Id, Key, +Value](
 edge: ImplNewEdge[Id, Key, Value]
) extends CreateOperation[ImplNewEdge[Id, Key, Value]]


case class ImplCreateVertexPropertyOperation[Key, +Value, +MetaValue](
  vertexProperty: ImplNewRecordRichProperty[Key, Value, MetaValue]
) extends CreateOperation[ImplNewRecordRichProperty[Key, Value, MetaValue]]

