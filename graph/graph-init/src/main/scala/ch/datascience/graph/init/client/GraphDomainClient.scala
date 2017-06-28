package ch.datascience.graph.init.client

import ch.datascience.graph.types.persistence.model.GraphDomain
import ch.datascience.graph.types.persistence.model.json.{GraphDomainFormat, GraphDomainRequestFormat}
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by johann on 22/06/17.
  */
class GraphDomainClient(val baseUrl: String, ws: WSClient) {

  def getOrCreateGraphDomain(name: String): Future[GraphDomain] = {
    for {
      opt <- getGraphDomain(name)
      pk <- opt match {
        case Some(pk@GraphDomain(_, n)) if name == n => Future.successful( pk )
        case Some(otherGD) => Future.failed( new RuntimeException(s"Expected graph domain: $name but got $otherGD") )
        case None => createGraphDomain(name)
      }
    } yield pk
  }

  def getGraphDomain(name: String): Future[Option[GraphDomain]] = {
    for {
      response <- ws.url(s"$baseUrl/management/graph_domain/$name").get()
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[GraphDomain](GraphDomainFormat)
        result match {
          case JsSuccess(graphDomain, _) => Some(graphDomain)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => throw new RuntimeException(response.statusText)
    }
  }

  def createGraphDomain(name: String): Future[GraphDomain] = {
    val body = Json.toJson(name)(GraphDomainRequestFormat)
    for {
      response <- ws.url(s"$baseUrl/management/graph_domain").post(body)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[GraphDomain](GraphDomainFormat)
        result match {
          case JsSuccess(graphDomain, _) => graphDomain
          case JsError(e) => throw JsResultException(e)
        }
      case _ => throw new RuntimeException(response.statusText)
    }
  }

}
