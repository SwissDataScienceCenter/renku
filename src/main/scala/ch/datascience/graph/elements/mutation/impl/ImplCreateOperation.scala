package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.mutation.CreateOperation
/**
  * Created by jeberle on 10.05.17.
  */
case class ImplCreateVertexOperation[TypeId, Key, +Value, MetaKey](
  vertex: ImplNewVertex[TypeId, Key, Value, MetaKey]
) extends CreateOperation[ImplNewVertex[TypeId, Key, Value, MetaKey]]

case class ImplCreateEdgeOperation[+Id, Key, +Value](
 edge: ImplNewEdge[Id, Key, Value]
) extends CreateOperation[ImplNewEdge[Id, Key, Value]]


case class ImplCreateVertexPropertyOperation[+Key, +Value, MetaKey, +MetaValue](
  vertexProperty: ImplNewRecordRichProperty[Key, Value, MetaKey, MetaValue]
) extends CreateOperation[ImplNewRecordRichProperty[Key, Value, MetaKey, MetaValue]]

