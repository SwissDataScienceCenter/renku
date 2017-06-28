package persistence.graph

import javax.inject.Inject

import ch.datascience.graph.execution.GraphExecutionContext
import org.apache.tinkerpop.gremlin.structure.Graph

/**
  * Created by johann on 13/06/17.
  */
class GraphExecutionContextProvider @Inject()(protected val janusGraphProvider: JanusGraphProvider) {

  lazy val ctxt: GraphExecutionContext = new GraphExecutionContext {
    protected def graph: Graph = janusGraphProvider.get
  }

  def get: GraphExecutionContext = ctxt

}
