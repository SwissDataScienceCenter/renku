package ch.datascience.graph.scope.persistence.relationaldb

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.scope.persistence.PersistedProperties
import ch.datascience.graph.types.PropertyKey

import scala.concurrent.Future

/**
  * Created by johann on 09/05/17.
  */
trait RelationalPersistedProperties extends PersistedProperties { this: ExecutionComponent with OrchestrationComponent =>

  /**
    * Fetches property key with specified key
    *
    * @param key
    * @return a future containing some property key if a corresponding one is found, None otherwise
    */
  def fetchPropertyFor(key: NamespaceAndName): Future[Option[PropertyKey]] = for {
    opt <- orchestrator.propertyKeys.findByNamespaceAndName(key)
  } yield for {
    propertyKey <- opt
  } yield propertyKey.toStandardPropertyKey

  /**
    * Grouped version of getPropertyFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param keys set of keys to retrieve
    * @return map key -> property key, will not contain unknown keys
    */
  def fetchPropertiesFor(keys: Set[NamespaceAndName]): Future[Map[PropertyKey#Key, PropertyKey]] = {
    val futurePropertyKeys = Future.traverse(keys.toIterable) { key =>
      for {
        opt <- orchestrator.propertyKeys.findByNamespaceAndName(key)
      } yield key -> opt
    }

    for {
      propertyKeys <- futurePropertyKeys
      iterable = for {
        (key, opt) <- propertyKeys
        propertyKey <- opt
      } yield key -> propertyKey.toStandardPropertyKey
    } yield iterable.toMap
  }

}
