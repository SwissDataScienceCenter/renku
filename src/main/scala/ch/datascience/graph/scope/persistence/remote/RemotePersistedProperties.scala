package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistedProperties
import ch.datascience.graph.scope.persistence.json.FetchPropertiesForResponseReads
import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.PropertyKeyFormat
import play.api.libs.json._
import play.api.libs.ws.WSResponse

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

/**
  * Created by johann on 17/05/17.
  */
trait RemotePersistedProperties extends PersistedProperties {

  /**
    * Fetches property key with specified key
    *
    * @param key
    * @return a future containing some property key if a corresponding one is found, None otherwise
    */
  final def fetchPropertyFor(key: PropertyKey#Key): Future[Option[PropertyKey]] = {
    for {
      response <- client.fetchPropertyForRemoteCall(key)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[PropertyKey]
        result match {
          case JsSuccess(propertyKey, _) => Some(propertyKey)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => cannotHandleResponse(response)
    }
  }

  /**
    * Grouped version of getPropertyFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param keys set of keys to retrieve
    * @return map key -> property key, will not contain unknown keys
    */
  final def fetchPropertiesFor(keys: Set[PropertyKey#Key]): Future[Map[PropertyKey#Key, PropertyKey]] = {
    for {
      response <- client.fetchPropertiesForRemoteCall(keys)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[Map[PropertyKey#Key, PropertyKey]]
        result match {
          case JsSuccess(propertyKeys, _) => propertyKeys
          case JsError(e) => throw JsResultException(e)
        }
      case _ => cannotHandleResponse(response)
    }
  }

  protected def client: ConfiguredClient

  protected def executionContext: ExecutionContext

  implicit lazy val ec: ExecutionContext = executionContext

  protected def keyReads: Reads[PropertyKey#Key]

  implicit lazy val propertyKeyReads = PropertyKeyFormat

  protected def cannotHandleResponse(response: WSResponse): Nothing = {
    throw new RuntimeException(s"Unexpected answer: HTTP${response.status} - ${response.statusText}, ${response.body}")
  }

  protected implicit lazy val fetchPropertiesForResponseReads: Reads[Map[PropertyKey#Key, PropertyKey]] = FetchPropertiesForResponseReads

}
