package ch.datascience.graph.typevalidation.scope.persistence.dummy

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.typevalidation.scope.persistence.PersistedProperties

import scala.concurrent.Future

/**
  * Created by johann on 08/05/17.
  */
case class DummyPersistedProperties[Key]() extends PersistedProperties[Key] {

  def fetchPropertyFor(key: Key): Future[Option[PropertyKey[Key]]] = Future.successful(None)

  def fetchPropertiesFor(keys: Set[Key]): Future[Map[Key, PropertyKey[Key]]] = Future.successful(Map.empty)

}
