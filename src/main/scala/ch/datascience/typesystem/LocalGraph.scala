package ch.datascience.typesystem

import org.janusgraph.core.{JanusGraph, JanusGraphFactory}

/**
  * Created by johann on 07/03/17.
  */
object LocalGraph {

  def loadGraph(): JanusGraph = JanusGraphFactory.open("./conf/janusgraph-berkeleyje-es.properties")

}
