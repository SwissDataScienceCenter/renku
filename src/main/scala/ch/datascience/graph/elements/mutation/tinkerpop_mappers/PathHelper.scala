package ch.datascience.graph.elements.mutation.tinkerpop_mappers

import ch.datascience.graph.elements.persisted._
import ch.datascience.graph.elements.tinkerpop_mappers.KeyWriter
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource}
import org.apache.tinkerpop.gremlin.structure.{Edge, Vertex}

/**
  * Created by johann on 19/06/17.
  */
object PathHelper {

  def follow(s: GraphTraversalSource, path: Path): GraphTraversal[_, _] = path match {
    case VertexPath(id) => s.V(Long.box(id))
    case EdgePath(id) => s.E(id)
    case PropertyPathFromRecord(parent, key) => follow(s, parent).properties(KeyWriter.write(key))
    case VertexPropertyPath(parent, id) => follow(s, parent).properties().hasId(id)
  }

}
