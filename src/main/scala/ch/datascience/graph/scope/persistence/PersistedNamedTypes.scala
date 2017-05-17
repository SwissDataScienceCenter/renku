package ch.datascience.graph.scope.persistence

import ch.datascience.graph.types.{NamedType, PropertyKey}

import scala.concurrent.Future

/**
  * Created by johann on 16/05/17.
  */
trait PersistedNamedTypes[TypeKey, PropKey] {

  /**
    * Fetches named type with specified key
    * @param key
    * @return a future containing some named type if a corresponding one is found, None otherwise
    */
  def fetchNamedTypeFor(key: TypeKey): Future[Option[NamedType[TypeKey, PropKey]]]


  /**
    * Grouped version of fetchNamedTypeFor
    *
    * If some keys are not found, they will not be part of the result map
    * @param keys set of keys to retrieve
    * @return map key -> named type, will not contain unknown keys
    */
  def fetchNamedTypesFor(keys: Set[TypeKey]): Future[Map[TypeKey, NamedType[TypeKey, PropKey]]]
  
}
