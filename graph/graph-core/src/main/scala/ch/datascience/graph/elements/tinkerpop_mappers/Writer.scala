package ch.datascience.graph.elements.tinkerpop_mappers

/**
  * Created by johann on 30/05/17.
  */
trait Writer[-From, +To] {

  def write(from: From): To

}
