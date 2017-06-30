package persistence.scope

import javax.inject.Inject

import ch.datascience.graph.scope.{Scope => Base}

/**
  * Created by johann on 13/06/17.
  */
class Scope @Inject()(override protected val persistenceLayer: RemotePersistenceLayer) extends Base(persistenceLayer = persistenceLayer)
