package ch.datascience.graph.scope.persistence.dummy

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.scope.persistence.PersistedProperties

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 08/05/17.
  */
case class DummyPersistedProperties[Key]() extends PersistedProperties[Key] {

  def fetchPropertyFor(key: Key)(implicit ec: ExecutionContext): Future[Option[PropertyKey[Key]]] = Future.successful(None)

  def fetchPropertiesFor(keys: Set[Key])(implicit ec: ExecutionContext): Future[Map[Key, PropertyKey[Key]]] = Future.successful(Map.empty)

}
