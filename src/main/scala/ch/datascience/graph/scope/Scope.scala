package ch.datascience.graph.scope

import ch.datascience.graph.scope.persistence.{PersistedProperties, PersistenceLayer}
import ch.datascience.graph.types.PropertyKey

import scala.collection.concurrent

/**
  * Created by johann on 11/05/17.
  */
class Scope[Key](protected val persistenceLayer: PersistenceLayer[Key]) extends PropertyScope[Key] {

  protected val propertyDefinitions: concurrent.TrieMap[Key, PropertyKey[Key]] = concurrent.TrieMap.empty

  protected def persistedProperties: PersistedProperties[Key] = persistenceLayer

}
