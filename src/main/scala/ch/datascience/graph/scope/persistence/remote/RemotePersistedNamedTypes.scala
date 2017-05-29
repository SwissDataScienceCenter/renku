package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.types.NamedType

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
trait RemotePersistedNamedTypes extends PersistedNamedTypes {

  /**
    * Fetches named type with specified key
    *
    * @param typeId
    * @return a future containing some named type if a corresponding one is found, None otherwise
    */
  final def fetchNamedTypeFor(typeId: NamedType#TypeId): Future[Option[NamedType]] = ???

  /**
    * Grouped version of fetchNamedTypeFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param typeIds set of keys to retrieve
    * @return map key -> named type, will not contain unknown keys
    */
  final def fetchNamedTypesFor(typeIds: Set[NamedType#TypeId]): Future[Map[NamedType#TypeId, NamedType]] = ???

}
