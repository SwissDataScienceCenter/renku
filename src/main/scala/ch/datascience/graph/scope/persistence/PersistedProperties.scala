package ch.datascience.graph.scope.persistence

import ch.datascience.graph.types.PropertyKey

import scala.concurrent.{ExecutionContext, Future}

/**
  * Base trait for accessing persisted property keys
  */
trait PersistedProperties {

  /**
    * Fetches property key with specified key
    * @param key
    * @return a future containing some property key if a corresponding one is found, None otherwise
    */
  def fetchPropertyFor(key: PropertyKey#Key): Future[Option[PropertyKey]]

  /**
    * Grouped version of getPropertyFor
    *
    * If some keys are not found, they will not be part of the result map
    * @param keys set of keys to retrieve
    * @return map key -> property key, will not contain unknown keys
    */
  def fetchPropertiesFor(keys: Set[PropertyKey#Key]): Future[Map[PropertyKey#Key, PropertyKey]]

}
