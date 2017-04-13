package ch.datascience.typesystem.orchestration

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 13/04/17.
  */
trait ExecutionComponent {

  protected val ec: ExecutionContext

  implicit val implicitEC: ExecutionContext = ec

}
