package ch.datascience.graph.types.persistence.orchestration

import ch.datascience.graph.types.persistence.graphdb.{GraphStack, ManagementActionRunner}
import ch.datascience.graph.types.persistence.relationaldb.DatabaseStack
import slick.basic.DatabaseConfig
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
    with NamedTypeComponent
    with EdgeLabelComponent
    with SystemPropertyKeyComponent
