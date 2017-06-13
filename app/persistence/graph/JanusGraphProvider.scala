package persistence.graph

import javax.inject.{Inject, Singleton}

import org.janusgraph.core.{JanusGraph, JanusGraphFactory}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Created by johann on 08/06/17.
  */
@Singleton
class JanusGraphProvider @Inject()(protected val config: JanusGraphConfigProvider, protected val lifecycle: ApplicationLifecycle) {

  lazy val graph: JanusGraph = JanusGraphFactory.open(config.get)

  def get: JanusGraph = graph

  lifecycle addStopHook { () =>
    Future.successful( graph.close() )
  }

}
