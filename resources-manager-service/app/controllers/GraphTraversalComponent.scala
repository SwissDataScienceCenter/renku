package controllers

import ch.datascience.graph.execution.GraphExecutionContext
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import play.api.mvc.Controller

/**
  * Created by johann on 13/06/17.
  */
trait GraphTraversalComponent { this: Controller =>

  protected def graphExecutionContextProvider: GraphExecutionContextProvider

  implicit protected def graphExecutionContext: GraphExecutionContext = graphExecutionContextProvider.get

  protected def janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider

  implicit protected def graphTraversalSource: GraphTraversalSource = janusGraphTraversalSourceProvider.get

}
