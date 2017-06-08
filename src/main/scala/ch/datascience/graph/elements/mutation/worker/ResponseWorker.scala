package ch.datascience.graph.elements.mutation.worker

import ch.datascience.graph.elements.mutation.log.dao.RequestDAO
import ch.datascience.graph.elements.mutation.log.model.Event
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 07/06/17.
  */
class ResponseWorker(
  protected val queue: Queue[JsValue],
  protected val ec: ExecutionContext
) {

  start()

  def start(): Unit = {
    Future.successful(()).map{ _ => this.work() }
  }

  def work(): Unit = {
    while (queue.nonEmpty) {
      val event = queue.dequeue
      processOneEvent(event)
    }
    queue.register().future.map{ _ => this.work() }
  }

  def processOneEvent(event: JsValue): Unit = {
//    println(s"TODO!! $event, Thread: ${java.lang.Thread.currentThread()}")
  }

  private[this] implicit lazy val e: ExecutionContext = ec

}
