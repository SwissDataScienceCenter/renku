package ch.datascience.graph.init.client

import ch.datascience.graph.types.persistence.model.RichPropertyKey
import ch.datascience.graph.types.persistence.model.json.{PropertyKeyFormat, PropertyKeyRequestFormat}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by johann on 22/06/17.
  */
class PropertyKeyClient(val baseUrl: String, ws: WSClient) {

  def getOrCreatePropertyKey(namespace: String, name: String, dataType: DataType, cardinality: Cardinality): Future[RichPropertyKey] = {
    for {
      opt <- getPropertyKey(namespace, name)
      pk <- opt match {
        case Some(pk@RichPropertyKey(_, gd, n, dt, c)) if namespace == gd.namespace && name == n && dataType == dt && cardinality == c => Future.successful( pk )
        case Some(otherPk) => Future.failed( new RuntimeException(s"Expected property key: ($namespace, $name, $dataType, $cardinality) but got $otherPk") )
        case None => createPropertyKey(namespace, name, dataType, cardinality)
      }
    } yield pk
  }

  def getPropertyKey(namespace: String, name: String): Future[Option[RichPropertyKey]] = {
    for {
      response <- ws.url(s"$baseUrl/management/property_key/$namespace/$name").get()
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[RichPropertyKey](PropertyKeyFormat)
        result match {
          case JsSuccess(propertyKey, _) => Some(propertyKey)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => throw new RuntimeException(response.statusText)
    }
  }

  def createPropertyKey(namespace: String, name: String, dataType: DataType, cardinality: Cardinality): Future[RichPropertyKey] = {
    val body = Json.toJson((namespace, name, dataType, cardinality))(PropertyKeyRequestFormat)
    for {
      response <- ws.url(s"$baseUrl/management/property_key").post(body)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[RichPropertyKey](PropertyKeyFormat)
        result match {
          case JsSuccess(propertyKey, _) => propertyKey
          case JsError(e) => throw JsResultException(e)
        }
      case _ => throw new RuntimeException(response.statusText)
    }
  }

}
