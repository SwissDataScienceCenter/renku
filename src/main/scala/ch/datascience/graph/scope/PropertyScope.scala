package ch.datascience.graph.scope

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.scope.persistence.PersistedProperties

import scala.collection.concurrent
import scala.concurrent.{ExecutionContext, Future}

/**
  * Base trait for scopes that handle property definitions
  * @tparam Key type of key
  */
trait PropertyScope[Key] {

  def getPropertyFor(key: Key)(implicit ec: ExecutionContext): Future[Option[PropertyKey[Key]]] = {
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

  def getPropertiesFor(keys: Set[Key])(implicit ec: ExecutionContext): Future[Map[Key, PropertyKey[Key]]] = {
    // Locally, sort known keys and unkown keys
    val tryLocally = (for (key <- keys) yield key -> propertyDefinitions.get(key)).toMap
    val knownPropertyKeys: Map[Key, PropertyKey[Key]] = for {
      (key, opt) <- tryLocally
      propertyKey <- opt
    } yield key -> propertyKey
    val unknownKeys: Set[Key] = for {
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

  protected def propertyDefinitions: concurrent.Map[Key, PropertyKey[Key]]

  protected def persistedProperties: PersistedProperties[Key]

}
