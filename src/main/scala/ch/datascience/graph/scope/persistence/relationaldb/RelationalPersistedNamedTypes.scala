package ch.datascience.graph.scope.persistence.relationaldb

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.types.NamedType

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
trait RelationalPersistedNamedTypes extends PersistedNamedTypes { this: ExecutionComponent with OrchestrationComponent =>

  /**
    * Fetches named type with specified key
    *
    * @param key
    * @return a future containing some named type if a corresponding one is found, None otherwise
    */
  def fetchNamedTypeFor(key: NamespaceAndName): Future[Option[NamedType]] =  for {
    opt <- orchestrator.namedTypes.findByNamespaceAndName(key)
  } yield for {
    namedType <- opt
  } yield namedType.toStandardNamedType

  /**
    * Grouped version of fetchNamedTypeFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param keys set of keys to retrieve
    * @return map key -> named type, will not contain unknown keys
    */
  def fetchNamedTypesFor(keys: Set[NamespaceAndName]): Future[Map[NamedType#TypeId, NamedType]] = {
    val futureNamedTypes = Future.traverse(keys.toIterable) { key =>
      for {
        opt <- orchestrator.namedTypes.findByNamespaceAndName(key)
      } yield key -> opt
    }

    for {
      namedTypes <- futureNamedTypes
      iterable = for {
        (key, opt) <- namedTypes
        namedType <- opt
      } yield key -> namedType.toStandardNamedType
    } yield iterable.toMap
  }

}
