package clients

import javax.inject.Inject

import ch.datascience.graph.elements.mutation.Mutation
import models.json._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class StorageClient @Inject()(implicit context: ExecutionContext, ws: WSClient, storageHost: String, token: String) {

  def write(name: String): Future[JsValue] = {
    val request: WSRequest = ws.url(storageHost + "/write/" + name)
      .withHeaders("Accept" -> "application/json", "Authorization" -> token)
      .withRequestTimeout(10000.millis)
    val futureResult: Future[JsValue] = request.get().map {
      response =>
        response.json
    }
    futureResult
  }
}

