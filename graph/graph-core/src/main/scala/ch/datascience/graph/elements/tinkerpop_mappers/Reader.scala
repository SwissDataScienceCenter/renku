package ch.datascience.graph.elements.tinkerpop_mappers

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 19/05/17.
  */
trait Reader[-From, +To] {

  def read(x: From)(implicit ec: ExecutionContext): Future[To]

}
