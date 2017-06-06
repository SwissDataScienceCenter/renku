package clients
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext

class ItemClient @Inject() (implicit context: ExecutionContext, ws: WSClient, host: String) {

  def list = {
    val request: WSRequest = ws.url(host + "/item")
      .withHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
    val futureResult: Future[String] = request.get().map {
      response =>
        response.json.as[String] //you can also map it back to objects
    }
  }

  def create = {

    val item = Json.obj(
      "key1" -> "value1",
      "key2" -> "value2"
    )
    val request: WSRequest = ws.url(host + "/item")
      .withHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
    val futureResult: Future[String] = request.post(item).map {
      response =>
        response.json.as[String] //you can also map it back to objects
    }
  }

  def update(id: Long) = {
    val item = Json.obj(
      "key1" -> "value1",
      "key2" -> "value2"
    )
    val request: WSRequest = ws.url(host + "/item")
      .withHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
      .withQueryString("id" -> id.toString)
    val futureResult: Future[String] = request.post(item).map {
      response =>
        response.json.as[String] //you can also map it back to objects
    }
  }

  def details(id: Long) = {
    val request: WSRequest = ws.url(host + "/item")
      .withHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
      .withQueryString("id" -> id.toString)
    val futureResult: Future[String] = request.get().map {
      response =>
        response.json.as[String] //you can also map it back to objects
    }
  }
}

