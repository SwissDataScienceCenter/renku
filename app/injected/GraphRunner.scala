package injected

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.types.persistence.graphdb.ManagementActionRunner
import org.janusgraph.core.{JanusGraph, JanusGraphFactory}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 13/04/17.
  */
@Singleton
class GraphRunner @Inject()(
                             protected val janusGraphConfig: JanusGraphConfig,
                             protected val lifecycle: ApplicationLifecycle
                           ) extends ManagementActionRunner {

  val graph: JanusGraph = JanusGraphFactory.open(janusGraphConfig.get)

  val ec: ExecutionContext = janusGraphConfig.getExecutionContext

  lifecycle addStopHook { () =>
    Future.successful { graph.close() }
  }

}
