package ch.datascience.graph.scope.persistence.relationaldb

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 09/05/17.
  */
trait ExecutionComponent {

  protected def ec: ExecutionContext

  implicit lazy val implicitEC: ExecutionContext = ec

}
