package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.scope.persistence.json.FetchNamedTypesForFormats
import ch.datascience.graph.types.NamedType
import ch.datascience.graph.types.json.NamedTypeFormat
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import play.api.libs.ws.WSResponse

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 17/05/17.
  */
trait RemotePersistedNamedTypes extends PersistedNamedTypes {

  // TODO: clean this

  /**
    * Fetches named type with specified key
    *
    * @param typeId
    * @return a future containing some named type if a corresponding one is found, None otherwise
    */
  final def fetchNamedTypeFor(typeId: NamedType#TypeId): Future[Option[NamedType]] = {
    for {
      response <- client.fetchNamedTypeForRemoteCall(typeId)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[NamedType](NamedTypeFormat)
        result match {
          case JsSuccess(propertyKey, _) => Some(propertyKey)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => cannotHandleResponse2(response)
    }
  }

  /**
    * Grouped version of fetchNamedTypeFor
    *
    * If some keys are not found, they will not be part of the result map
    *
    * @param typeIds set of keys to retrieve
    * @return map key -> named type, will not contain unknown keys
    */
  final def fetchNamedTypesFor(typeIds: Set[NamedType#TypeId]): Future[Map[NamedType#TypeId, NamedType]] = {
    for {
      response <- client.fetchNamedTypesForRemoteCall(typeIds)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[Map[NamedType#TypeId, NamedType]](FetchNamedTypesForFormats.ResponseFormat)
        result match {
          case JsSuccess(namedTypes, _) => namedTypes
          case JsError(e) => throw JsResultException(e)
        }
      case _ => cannotHandleResponse2(response)
    }
  }

  protected def client: ConfiguredClient

  protected def executionContext: ExecutionContext

  implicit lazy val ec2: ExecutionContext = executionContext

  protected def cannotHandleResponse2(response: WSResponse): Nothing = {
    throw new RuntimeException(s"Unexpected answer: HTTP${response.status} - ${response.statusText}, ${response.body}")
  }

}
