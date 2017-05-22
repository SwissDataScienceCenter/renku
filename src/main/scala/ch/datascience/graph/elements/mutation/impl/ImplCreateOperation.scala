package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.mutation.CreateOperation
/**
  * Created by jeberle on 10.05.17.
  */
case class ImplCreateVertexOperation[TypeId, Key, +Value](
  vertex: ImplNewVertex[TypeId, Key, Value]
) extends CreateOperation[ImplNewVertex[TypeId, Key, Value]]

case class ImplCreateEdgeOperation[+Id, Key, +Value](
 edge: ImplNewEdge[Id, Key, Value]
) extends CreateOperation[ImplNewEdge[Id, Key, Value]]


case class ImplCreateVertexPropertyOperation[Key, +Value, +MetaValue](
  vertexProperty: ImplNewRecordRichProperty[Key, Value, MetaValue]
) extends CreateOperation[ImplNewRecordRichProperty[Key, Value, MetaValue]]

