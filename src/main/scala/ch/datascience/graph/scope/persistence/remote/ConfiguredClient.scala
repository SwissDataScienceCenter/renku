package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.types.{NamedType, PropertyKey}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
trait ConfiguredClient {

  protected def wsClient: WSClient

  def fetchPropertyForRemoteCall(key: PropertyKey#Key): Future[WSResponse]

  def fetchPropertiesForRemoteCall(keys: Set[PropertyKey#Key]): Future[WSResponse]

  def fetchNamedTypeForRemoteCall(key: NamedType#TypeId): Future[WSResponse]

  def fetchNamedTypesForRemoteCall(keys: Set[NamedType#TypeId]): Future[WSResponse]

  def close(): Unit = {
    wsClient.close()
  }

}
