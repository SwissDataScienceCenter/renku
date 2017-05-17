package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.types.NamedType

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
trait RemotePersistedNamedTypes[TypeKey, PropKey] extends PersistedNamedTypes[TypeKey, PropKey] {

  /**
    * Fetches named type with specified key
    *
    * @param key
    * @return a future containing some named type if a corresponding one is found, None otherwise
    */
  final def fetchNamedTypeFor(key: TypeKey): Future[Option[NamedType[TypeKey, PropKey]]] = ???

  /**
    * Grouped version of fetchNamedTypeFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param keys set of keys to retrieve
    * @return map key -> named type, will not contain unknown keys
    */
  final def fetchNamedTypesFor(keys: Set[TypeKey]): Future[Map[TypeKey, NamedType[TypeKey, PropKey]]] = ???

}
