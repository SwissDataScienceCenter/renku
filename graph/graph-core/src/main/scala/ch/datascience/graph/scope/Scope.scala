package ch.datascience.graph.scope

import ch.datascience.graph.scope.persistence.{PersistedNamedTypes, PersistedProperties, PersistenceLayer}
import ch.datascience.graph.types.{NamedType, PropertyKey}

import scala.collection.concurrent

/**
  * Created by johann on 11/05/17.
  */
class Scope(protected val persistenceLayer: PersistenceLayer)
  extends PropertyScope with NamedTypeScope {

  protected val propertyDefinitions: concurrent.TrieMap[PropertyKey#Key, PropertyKey] = concurrent.TrieMap.empty

  protected def persistedProperties: PersistedProperties = persistenceLayer

  protected val namedTypeDefinitions: concurrent.TrieMap[NamedType#TypeId, NamedType] = concurrent.TrieMap.empty

  protected def persistedNamedTypes: PersistedNamedTypes = persistenceLayer
}
