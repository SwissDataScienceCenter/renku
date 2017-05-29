package ch.datascience.graph.scope.persistence.dummy

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.scope.persistence.PersistedProperties

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 08/05/17.
  */
case class DummyPersistedProperties() extends PersistedProperties {

  def fetchPropertyFor(key: PropertyKey#Key): Future[Option[PropertyKey]] = Future.successful(None)

  def fetchPropertiesFor(keys: Set[PropertyKey#Key]): Future[Map[PropertyKey#Key, PropertyKey]] = Future.successful(Map.empty)

}
