package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistedProperties
import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.PropertyKeyReads
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

/**
  * Created by johann on 17/05/17.
  */
trait RemotePersistedProperties[Key] extends PersistedProperties[Key] {

  /**
    * Fetches property key with specified key
    *
    * @param key
    * @return a future containing some property key if a corresponding one is found, None otherwise
    */
  final def fetchPropertyFor(key: Key): Future[Option[PropertyKey[Key]]] = {
    for {
      response <- client.fetchPropertyForRemoteCall(key)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[PropertyKey[Key]]
        result match {
          case JsSuccess(propertyKey, _) => Some(propertyKey)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => throw new RuntimeException(s"Unexpected answer: HTTP${response.status} - ${response.statusText}, ${response.body}")
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
  final def fetchPropertiesFor(keys: Set[Key]): Future[Map[Key, PropertyKey[Key]]] = ???

  protected def client: ConfiguredClient[_, Key]

  protected def executionContext: ExecutionContext

  implicit lazy val ec: ExecutionContext = executionContext

  protected def keyReads: Reads[Key]

  implicit lazy val propertyKeyReads = new PropertyKeyReads[Key]()(keyReads)

}
