package ch.datascience.graph.scope.persistence

/**
  * Created by johann on 11/05/17.
  */
abstract class PersistenceLayer[TypeKey, PropKey]
  extends PersistedProperties[PropKey]
    with PersistedNamedTypes[TypeKey, PropKey]
