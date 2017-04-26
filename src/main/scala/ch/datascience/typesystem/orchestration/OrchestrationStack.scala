package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.graphdb.{GraphStack, ManagementActionRunner}
import ch.datascience.typesystem.model.base.{GraphObjectBase, NamedRecordTypeBase}
import ch.datascience.typesystem.model.{PropertyKey, RecordType}
import ch.datascience.typesystem.relationaldb.DatabaseStack
import ch.datascience.typesystem.scope.ConcurrentScope
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 13/04/17.
  */
class OrchestrationStack(
                          protected val ec: ExecutionContext,
                          protected val dbConfig: DatabaseConfig[JdbcProfile],
                          protected val dal: DatabaseStack,
                          protected val gdb: ManagementActionRunner,
                          protected val gal: GraphStack,
                          protected val scope: ScopeComponent#ConcurrentScopeType
                        )
  extends ExecutionComponent
    with DatabaseComponent
    with GraphComponent
    with GraphDomainComponent
    with PropertyKeyComponent
    with ScopeComponent
