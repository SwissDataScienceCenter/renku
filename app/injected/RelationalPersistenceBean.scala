package injected

import javax.inject.Inject

import ch.datascience.graph.scope.persistence.relationaldb.RelationalPersistenceLayer
import ch.datascience.graph.types.persistence.orchestration.OrchestrationStack

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 11/05/17.
  */
class RelationalPersistenceBean @Inject()(
  override protected val orchestrator: OrchestrationLayer
) extends RelationalPersistenceLayer(
  ec = play.api.libs.concurrent.Execution.defaultContext,
  orchestrator = orchestrator
)
