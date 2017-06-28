package ch.datascience.graph.scope.persistence.relationaldb

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.scope.persistence.PersistenceLayer
import ch.datascience.graph.types.{NamedType, PropertyKey}
import ch.datascience.graph.types.persistence.orchestration.OrchestrationStack

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 11/05/17.
  */
class RelationalPersistenceLayer(
  protected val ec: ExecutionContext,
  protected val orchestrator: OrchestrationStack
) extends PersistenceLayer
    with RelationalPersistedProperties
    with RelationalPersistedNamedTypes
    with ExecutionComponent
    with OrchestrationComponent
