package ch.datascience.graph.elements.tinkerpop_mappers

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
trait SyncReader[-From, +To] extends Reader[From, To] {

  final def read(x: From)(implicit ec: ExecutionContext): Future[To] = Future.successful(readSync(x))

  def readSync(x: From): To

}
