package persistence.scope

import javax.inject.Inject

import ch.datascience.graph.scope.persistence.remote.StandardRemotePersistenceLayer
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  * Created by johann on 13/06/17.
  */
class RemotePersistenceLayer @Inject()(
  override val client: ScopeClient
) extends StandardRemotePersistenceLayer(client = client)
