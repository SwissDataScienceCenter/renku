package ch.datascience.graph.elements.tinkerpop_mappers

/**
  * Created by johann on 19/05/17.
  */
trait KeyValueReader[-Key, +To] extends Reader[(Key, Any), To]
