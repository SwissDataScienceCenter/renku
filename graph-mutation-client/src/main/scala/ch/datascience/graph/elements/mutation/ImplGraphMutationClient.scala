package ch.datascience.graph.elements.mutation

import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeoutException
import scala.concurrent.blocking
import ch.datascience.graph.elements.mutation.json.MutationFormat
import ch.datascience.graph.elements.mutation.log.model.json._
import ch.datascience.graph.elements.mutation.log.model.{Event, EventStatus}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Created by johann on 29/06/17.
  */
class ImplGraphMutationClient(val baseUrl: String, context: ExecutionContext, ws: WSClient) extends GraphMutationClient {

  def post(mutation: Mutation): Future[Event] = {
    val request = ws.url(s"$baseUrl/mutation").withHeaders("Accept" -> "application/json").withRequestTimeout(10.seconds)
    for {
      response <- request.post(Json.toJson(mutation)(MutationFormat))
    } yield response.json.as[Event]
  }

  def wait(uuid: UUID, timeout: Option[Deadline]): Future[EventStatus] = {
    for {
      eventStatus <- status(uuid)
      result <- eventStatus.status match {
        case EventStatus.Completed(response) => Future.successful( eventStatus )
        case EventStatus.Pending => timeout match {
          case Some(t) =>
            if (t.hasTimeLeft())
              Future { blocking ( Thread.sleep(1000) ) }.flatMap{ _ => wait(uuid, Some(t)) }
            else
              throw new TimeoutException()
          case None => wait(uuid, None)
        }
      }
    } yield result
  }

  def status(uuid: UUID): Future[EventStatus] = {
    val request = ws.url(s"$baseUrl/mutation/$uuid").withHeaders("Accept" -> "application/json").withRequestTimeout(10.seconds)
    for {
      response <- request.get()
    } yield response.json.as[EventStatus]
  }

  private[this] implicit lazy val ex: ExecutionContext = context

}
