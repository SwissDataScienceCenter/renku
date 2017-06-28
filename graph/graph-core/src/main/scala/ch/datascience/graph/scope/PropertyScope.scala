package ch.datascience.graph.scope

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.scope.persistence.PersistedProperties

import scala.collection
import scala.collection.concurrent
import scala.concurrent.{ExecutionContext, Future}

/**
  * Base trait for scopes that handle property definitions
  * @tparam Key type of key
  */
trait PropertyScope {

  final def getPropertyFor(key: PropertyKey#Key)(implicit ec: ExecutionContext): Future[Option[PropertyKey]] = {
    propertyDefinitions get key match {
      case Some(propertyKey) => Future.successful( Some(propertyKey) )
      case None => {
        val result = persistedProperties.fetchPropertyFor(key)

        // If we get a property key, then we add it to our scope
        result.onSuccess({
          case Some(propertyKey) => propertyDefinitions.put(propertyKey.key, propertyKey)
        })(ec)

        result
      }
    }
  }

  final def getPropertiesFor(keys: Set[PropertyKey#Key])(implicit ec: ExecutionContext): Future[Map[PropertyKey#Key, PropertyKey]] = {
    // Locally, sort known keys and unkown keys
    val tryLocally = (for (key <- keys) yield key -> propertyDefinitions.get(key)).toMap
    val knownPropertyKeys: Map[PropertyKey#Key, PropertyKey] = for {
      (key, opt) <- tryLocally
      propertyKey <- opt
    } yield key -> propertyKey
    val unknownKeys: Set[PropertyKey#Key] = for {
      key <- tryLocally.keySet
      if tryLocally(key).isEmpty
    } yield key

    // Resolve unknown keys
    val resolved = persistedProperties.fetchPropertiesFor(unknownKeys)

    // Update resolved keys
    resolved.map({ definitions =>
      for (propertyKey <- definitions.values) {
        propertyDefinitions.put(propertyKey.key, propertyKey)
      }
    })(ec)

    resolved.map({ definitions =>
      knownPropertyKeys ++ definitions
    })(ec)
  }

  final def getCachedProperties: Future[collection.Map[PropertyKey#Key, PropertyKey]] = {
    Future.successful( propertyDefinitions.readOnlySnapshot() )
  }

  protected def propertyDefinitions: concurrent.TrieMap[PropertyKey#Key, PropertyKey]

  protected def persistedProperties: PersistedProperties

}
