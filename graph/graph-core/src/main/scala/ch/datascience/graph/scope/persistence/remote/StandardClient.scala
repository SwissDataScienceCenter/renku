package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.scope.persistence.json._
import ch.datascience.graph.types.NamedType
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

/**
  * Created by johann on 17/05/17.
  */
class StandardClient(val wsClient: WSClient, val baseUrl: String) extends ConfiguredClient {

  def fetchPropertyForRemoteCall(key: NamespaceAndName): Future[WSResponse] = {
    val NamespaceAndName(namespace, name) = key
    val request = wsClient.url(s"$baseUrl/scope/property/$namespace/$name")
    request.get()
  }

  def fetchPropertiesForRemoteCall(keys: Set[NamespaceAndName]): Future[WSResponse] = {
    val request = wsClient.url(s"$baseUrl/scope/property")
    val body = Json.toJson(keys)
    request.post(body)
  }

  def fetchNamedTypeForRemoteCall(key: NamedType#TypeId): Future[WSResponse] = {
    val NamespaceAndName(namespace, name) = key
    val request = wsClient.url(s"$baseUrl/scope/type/$namespace/$name")
    request.get()
  }

  def fetchNamedTypesForRemoteCall(keys: Set[NamespaceAndName]): Future[WSResponse] = {
    val request = wsClient.url(s"$baseUrl/scope/type")
    val body = Json.toJson(keys)
    request.post(body)
  }

}

object StandardClient {

  def makeStandaloneClient(baseUrl: String): StandardClient = {
    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    import play.api.libs.ws.ahc.AhcWSClient

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val wsClient = AhcWSClient()

    new StandardClient(wsClient, baseUrl)
  }

}
