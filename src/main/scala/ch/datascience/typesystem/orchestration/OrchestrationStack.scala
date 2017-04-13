package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.graphdb.{GraphStack, ManagementActionRunner}
import ch.datascience.typesystem.model.table.DatabaseStack
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
                          protected val gal: GraphStack
                        )
  extends ExecutionComponent
    with DatabaseComponent
    with GraphComponent
    with GraphDomainComponent
    with PropertyKeyComponent
