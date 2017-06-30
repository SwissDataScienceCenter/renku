package persistence.graph

import javax.inject.Inject

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy

/**
  * Created by johann on 13/06/17.
  */
class JanusGraphTraversalSourceProvider @Inject()(protected val janusGraphProvider: JanusGraphProvider) {

  /**
    *
    * @return read-only traversal source
    */
  def get: GraphTraversalSource = {
    val graph = janusGraphProvider.get
    graph.traversal().withStrategies(ReadOnlyStrategy.instance())
  }

}
