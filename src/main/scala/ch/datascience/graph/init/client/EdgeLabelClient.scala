package ch.datascience.graph.init.client

import ch.datascience.graph.types.persistence.model.RichEdgeLabel
import ch.datascience.graph.types.persistence.model.json.{EdgeLabelFormat, EdgeLabelRequestFormat}
import ch.datascience.graph.types.{Cardinality, DataType, Multiplicity}
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by johann on 22/06/17.
  */
class EdgeLabelClient(val baseUrl: String, ws: WSClient) {

  def getOrCreateEdgeLabel(namespace: String, name: String, multiplicity: Multiplicity): Future[RichEdgeLabel] = {
    for {
      opt <- getEdgeLabel(namespace, name)
      pk <- opt match {
        case Some(el@RichEdgeLabel(_, gd, n, m)) if namespace == gd.namespace && name == n && multiplicity == m => Future.successful( el )
        case Some(otherEL) => Future.failed( new RuntimeException(s"Expected property key: ($namespace, $name, $multiplicity) but got $otherEL") )
        case None => createEdgeLabel(namespace, name, multiplicity)
      }
    } yield pk
  }

  def getEdgeLabel(namespace: String, name: String): Future[Option[RichEdgeLabel]] = {
    for {
      response <- ws.url(s"$baseUrl/management/edge_label/$namespace/$name").get()
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[RichEdgeLabel](EdgeLabelFormat)
        result match {
          case JsSuccess(edgeLabel, _) => Some(edgeLabel)
          case JsError(e) => throw JsResultException(e)
        }
      case 404 => None
      case _ => throw new RuntimeException(response.statusText)
    }
  }

  def createEdgeLabel(namespace: String, name: String, multiplicity: Multiplicity): Future[RichEdgeLabel] = {
    val body = Json.toJson((namespace, name, multiplicity))(EdgeLabelRequestFormat)
    for {
      response <- ws.url(s"$baseUrl/management/edge_label").post(body)
    } yield response.status match {
      case 200 =>
        val result = response.json.validate[RichEdgeLabel](EdgeLabelFormat)
        result match {
          case JsSuccess(edgeLabel, _) => edgeLabel
          case JsError(e) => throw JsResultException(e)
        }
      case _ => throw new RuntimeException(response.statusText)
    }
  }

}
