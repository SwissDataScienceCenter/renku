package ch.datascience.graph.elements.tinkerpop_mappers.extractors

/**
  * Created by johann on 30/05/17.
  */
trait Extractor[-From, +To] {

  def apply(from: From): To

}
