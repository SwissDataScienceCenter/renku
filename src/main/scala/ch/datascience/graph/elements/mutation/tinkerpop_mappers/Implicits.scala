package ch.datascience.graph.elements.mutation.tinkerpop_mappers

import ch.datascience.graph.elements.mutation.create.CreateVertexOperation
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource}
import org.apache.tinkerpop.gremlin.structure.Vertex

/**
  * Created by johann on 30/05/17.
  */
object Implicits {

  implicit class RichCreateVertexOperation(op: CreateVertexOperation) {
    def toTraversalOperation: (GraphTraversalSource) => GraphTraversal[Vertex, Vertex] = CreateVertexOperationMapper(op)
  }

}
