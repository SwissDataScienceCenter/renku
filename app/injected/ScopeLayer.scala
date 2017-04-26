package injected

import javax.inject.Singleton

import ch.datascience.typesystem.orchestration.OrchestrationStack

/**
  * Created by johann on 26/04/17.
  */
@Singleton
class ScopeLayer extends ScopeLayer.ConcurrentScopeType

object ScopeLayer {

  type ConcurrentScopeType = OrchestrationStack#ConcurrentScopeType

}
