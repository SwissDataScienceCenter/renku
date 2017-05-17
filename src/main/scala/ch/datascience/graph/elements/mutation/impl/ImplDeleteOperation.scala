package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.mutation.DeleteOperation
import ch.datascience.graph.elements.persistence._

/**
  * Created by jeberle on 10.05.17.
  */

case class ImplDeleteVertexOperation[+Id](
  vertex: VertexPath[Id]
) extends DeleteOperation[VertexPath[Id]]

case class ImplDeleteEdgeOperation[+Id, +VertexId](
  edge: EdgePath[VertexId, Id]
) extends DeleteOperation[EdgePath[VertexId, Id]]


case class ImplDeleteVertexPropertyOperation[+Key](
  property: PropertyPathFromRecord[Key]
) extends DeleteOperation[PropertyPathFromRecord[Key]]

