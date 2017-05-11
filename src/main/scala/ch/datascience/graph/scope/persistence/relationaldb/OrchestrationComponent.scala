package ch.datascience.graph.scope.persistence.relationaldb

import ch.datascience.graph.types.persistence.orchestration.OrchestrationStack

/**
  * Created by johann on 09/05/17.
  */
trait OrchestrationComponent {

  protected def orchestrator: OrchestrationStack

}
