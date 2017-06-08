package models

import javax.inject.Inject
import javax.inject.Singleton

import org.janusgraph.core.{JanusGraph, JanusGraphFactory}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Created by johann on 08/06/17.
  */
@Singleton
class GraphProvider @Inject()(protected val config: JanusGraphConfig, protected val lifecycle: ApplicationLifecycle) {

  lazy val graph: JanusGraph = JanusGraphFactory.open(config.get)

  lifecycle addStopHook { () =>
    Future.successful { graph.close() }
  }

}
