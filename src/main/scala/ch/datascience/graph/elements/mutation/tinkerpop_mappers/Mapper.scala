package ch.datascience.graph.elements.mutation.tinkerpop_mappers

import ch.datascience.graph.elements.mutation.Operation
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

/**
  * Created by johann on 30/05/17.
  */
trait Mapper {

  type OperationType <: Operation

  type Source
  type Element

  def apply(op: OperationType): (GraphTraversalSource) => Traversal[Source, Element]

}
