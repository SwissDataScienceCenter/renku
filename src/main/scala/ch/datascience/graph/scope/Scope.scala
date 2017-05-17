package ch.datascience.graph.scope

import ch.datascience.graph.scope.persistence.{PersistedNamedTypes, PersistedProperties, PersistenceLayer}
import ch.datascience.graph.types.{NamedType, PropertyKey}

import scala.collection.concurrent

/**
  * Created by johann on 11/05/17.
  */
class Scope[TypeKey, PropKey](protected val persistenceLayer: PersistenceLayer[TypeKey, PropKey])
  extends PropertyScope[PropKey] with NamedTypeScope[TypeKey, PropKey] {

  protected val propertyDefinitions: concurrent.TrieMap[PropKey, PropertyKey[PropKey]] = concurrent.TrieMap.empty

  protected def persistedProperties: PersistedProperties[PropKey] = persistenceLayer

  protected val namedTypeDefinitions: concurrent.TrieMap[TypeKey, NamedType[TypeKey, PropKey]] = concurrent.TrieMap.empty

  protected def persistedNamedTypes: PersistedNamedTypes[TypeKey, PropKey] = persistenceLayer
}
