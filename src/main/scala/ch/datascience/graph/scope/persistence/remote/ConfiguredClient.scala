package ch.datascience.graph.scope.persistence.remote

import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
trait ConfiguredClient[TypeKey, PropKey] {

  protected def wsClient: WSClient

  def fetchPropertyForRemoteCall(key: PropKey): Future[WSResponse]

  def fetchPropertiesForRemoteCall(keys: Set[PropKey]): Future[WSResponse]

}
