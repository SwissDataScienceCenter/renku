package ch.datascience.graph.scope.persistence.relationaldb

import ch.datascience.graph.NamespaceAndName
import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.types.NamedType

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
trait RelationalPersistedNamedTypes extends PersistedNamedTypes[NamespaceAndName, NamespaceAndName] {

  // TODO: implementations

  /**
    * Fetches named type with specified key
    *
    * @param key
    * @return a future containing some named type if a corresponding one is found, None otherwise
    */
  def fetchNamedTypeFor(key: NamespaceAndName): Future[Option[NamedType[NamespaceAndName, NamespaceAndName]]] = ???

  /**
    * Grouped version of fetchNamedTypeFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param keys set of keys to retrieve
    * @return map key -> named type, will not contain unknown keys
    */
  def fetchNamedTypesFor(keys: Set[NamespaceAndName]): Future[Map[NamespaceAndName, NamedType[NamespaceAndName, NamespaceAndName]]] = ???

}
