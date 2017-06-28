package clients
import javax.inject.Inject

import ch.datascience.graph.elements.mutation.Mutation
import models.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext

class GraphClient @Inject() (implicit context: ExecutionContext, ws: WSClient, host: String) {

  def status(id: String): Future[JsValue] = {
    val request: WSRequest = ws.url(host + "/mutation/" + id)
      .withHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
    val futureResult: Future[JsValue] = request.get().map {
      response =>
        response.json
    }
    futureResult
  }

  def create(mutation: Mutation): Future[JsValue] = {

    val request: WSRequest = ws.url(host + "/mutation")
      .withHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
    val futureResult: Future[JsValue] = request.post(Json.toJson(mutation)).map {
      response =>
        response.json
    }
    futureResult
  }
}

