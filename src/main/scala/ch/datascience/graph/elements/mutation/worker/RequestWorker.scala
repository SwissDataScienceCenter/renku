package ch.datascience.graph.elements.mutation.worker

import java.util.UUID

import ch.datascience.graph.elements.mutation.log.dao.RequestDAO
import ch.datascience.graph.elements.mutation.log.model.Event
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 07/06/17.
  */
class RequestWorker(
  protected val queue: Queue[(UUID, JsValue)],
  protected val dao: RequestDAO,
  protected val ec: ExecutionContext
) {

  def add(event: JsValue): Future[Event] = dao.add(event).map { event =>
    queue.enqueue((event.uuid, event.event))
    event
  }

  private[this] implicit lazy val e: ExecutionContext = ec

}
