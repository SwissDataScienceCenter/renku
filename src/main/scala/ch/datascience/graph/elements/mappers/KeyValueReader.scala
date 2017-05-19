package ch.datascience.graph.elements.mappers

/**
  * Created by johann on 19/05/17.
  */
trait KeyValueReader[-Key, +To] extends Reader[(Key, java.lang.Object), To]
