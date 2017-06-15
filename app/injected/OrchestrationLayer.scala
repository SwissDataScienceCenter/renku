package injected

import javax.inject.Inject

import ch.datascience.graph.types.persistence.orchestration.OrchestrationStack
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 13/04/17.
  */
class OrchestrationLayer @Inject()(
                                    @NamedDatabase("sqldb") protected val dbConfigProvider : DatabaseConfigProvider,
                                    override protected val dal: DatabaseLayer,
                                    override protected val gal: GraphLayer,
                                    override protected val gdb: GraphRunner
                                  )
  extends OrchestrationStack(
    ec = play.api.libs.concurrent.Execution.defaultContext,
    dbConfig = dbConfigProvider.get,
    dal = dal,
    gdb = gdb,
    gal = gal
  )
