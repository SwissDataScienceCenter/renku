package ch.datascience.graph.init.client

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.persistence.model.RichNamedType
import ch.datascience.graph.types.persistence.model.json.{NamedTypeFormat, NamedTypeRequestFormat}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by johann on 22/06/17.
  */
class NamedTypeClient(val baseUrl: String, ws: WSClient) {

  def getOrCreateNamedType(namespace: String, name: String, superTypes: Seq[NamespaceAndName], properties: Seq[NamespaceAndName]): Future[RichNamedType] = {
    for {
      opt <- getNamedType(namespace, name)
      pk <- opt match {
        case Some(nt@RichNamedType(_, gd, n, st, p)) if namespace == gd.namespace && name == n && st.keySet == superTypes.toSet && p.keySet == properties.toSet => Future.successful( nt )
        case Some(otherNT) => Future.failed( new RuntimeException(s"Expected named type: ($namespace, $name, $superTypes, $properties) but got $otherNT") )
        case None => createNamedType(namespace, name, superTypes, properties)
      }
    } yield pk
  }

  def getNamedType(namespace: String, name: String): Future[Option[RichNamedType]] = {
    for {
      response <- ws.url(s"$baseUrl/management/named_type/$namespace/$name").get()
    } yield response.status match {
      case 200 =>
        println(response.json)
        val result = response.json.validate[RichNamedType](NamedTypeFormat)
        result match {
          case JsSuccess(propertyKey, _) => Some(propertyKey)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => throw new RuntimeException(response.statusText)
    }
  }

  def createNamedType(namespace: String, name: String, superTypes: Seq[NamespaceAndName], properties: Seq[NamespaceAndName]): Future[RichNamedType] = {
    val body = Json.toJson((namespace, name, superTypes, properties))(NamedTypeRequestFormat)
    for {
      response <- ws.url(s"$baseUrl/management/named_type").post(body)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[RichNamedType](NamedTypeFormat)
        result match {
          case JsSuccess(propertyKey, _) => propertyKey
          case JsError(e) => throw JsResultException(e)
        }
      case _ => throw new RuntimeException(response.statusText)
    }
  }

}
