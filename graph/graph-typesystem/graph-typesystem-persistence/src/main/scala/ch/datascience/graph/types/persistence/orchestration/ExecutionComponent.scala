package ch.datascience.graph.types.persistence.orchestration

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 13/04/17.
  */
trait ExecutionComponent {

  protected def ec: ExecutionContext

  implicit lazy val implicitEC: ExecutionContext = ec

}
