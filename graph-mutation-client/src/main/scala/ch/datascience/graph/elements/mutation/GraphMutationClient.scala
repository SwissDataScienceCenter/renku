package ch.datascience.graph.elements.mutation

import java.util.UUID

import ch.datascience.graph.elements.mutation.log.model.{Event, EventStatus}
import play.api.libs.ws.WSClient

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Created by johann on 28/06/17.
  */
trait GraphMutationClient {

  def baseUrl: String

  def post(mutation: Mutation): Future[Event]

  /**
    *
    * @param uuid
    * @param timeout soft timeout, won't attempt again if expired
    * @return
    */
  def wait(uuid: UUID, timeout: Option[Deadline] = Some(1.minute.fromNow)): Future[EventStatus]

  final def wait(uuid: UUID, timeout: Duration)(implicit ec: ExecutionContext): Future[EventStatus] = timeout match {
    case d: FiniteDuration => wait(uuid, Some(d.fromNow))
    case _ => Future {
      Await.result(wait(uuid, None), timeout)
    }
  }

  def status(uuid: UUID): Future[EventStatus]

}

object GraphMutationClient {

  def apply(baseUrl: String, context: ExecutionContext, ws: WSClient): GraphMutationClient = new ImplGraphMutationClient(baseUrl, context, ws)

  def makeStandaloneClient(baseUrl: String): GraphMutationClient with AutoCloseable = {
    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    import play.api.libs.ws.ahc.AhcWSClient

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val wsClient = AhcWSClient()

    val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

    class StandaloneGraphMutationClient extends ImplGraphMutationClient(baseUrl, ec, wsClient) with AutoCloseable {
      def close(): Unit = {
        wsClient.close()
        materializer.shutdown()
        system.terminate()
      }
    }

    new StandaloneGraphMutationClient
  }

}
