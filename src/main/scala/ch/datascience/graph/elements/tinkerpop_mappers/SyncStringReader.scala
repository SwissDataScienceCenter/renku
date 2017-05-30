package ch.datascience.graph.elements.tinkerpop_mappers

/**
  * Created by johann on 30/05/17.
  */
trait SyncStringReader[+To] extends StringReader[To] with SyncReader[String, To]
