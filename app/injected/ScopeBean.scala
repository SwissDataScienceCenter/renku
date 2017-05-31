package injected

import javax.inject.Inject

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.scope.Scope

/**
  * Created by johann on 11/05/17.
  */
class ScopeBean @Inject()(
  override protected val persistenceLayer: RelationalPersistenceBean
) extends Scope(persistenceLayer = persistenceLayer)
