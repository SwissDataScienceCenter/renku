package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.DatabaseSetup
import ch.datascience.typesystem.external.DatabaseConfigComponent
import ch.datascience.typesystem.graphdb.{GraphStack, ManagementActionRunner}
import ch.datascience.typesystem.relationaldb.DatabaseStack
import ch.datascience.typesystem.scope.ConcurrentScope
import com.typesafe.config.ConfigFactory
import org.janusgraph.core.{JanusGraph, JanusGraphFactory}
import org.scalatest.{AsyncTestSuite, BeforeAndAfterAll, BeforeAndAfterEach}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

/**
  * Created by johann on 13/04/17.
  */
trait OrchestrationSetup extends DatabaseSetup with BeforeAndAfterEach with BeforeAndAfterAll { this : AsyncTestSuite =>

  import profile.api._

  //TODO: Fix thread starvation with graph (Probably ManagementActionRunner)

  lazy val jgraph: JanusGraph = JanusGraphFactory.open("src/test/resources/janusgraph-berkeleyje-es.properties")

  lazy val gdb = new ManagementActionRunner {
    override protected lazy val graph: JanusGraph = jgraph
    override protected lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  }

  lazy val gal = new GraphStack()

  type ConcurrentScopeType = OrchestrationStack#ConcurrentScopeType
  lazy val scope: OrchestrationStack#ConcurrentScopeType = new ConcurrentScopeType()

  lazy val ol = new OrchestrationStack(scala.concurrent.ExecutionContext.global, dbConfig, dal, gdb, gal, scope)

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally {
      jgraph.close()
      org.janusgraph.core.util.JanusGraphCleanup.clear(jgraph)
    }
  }

//  override def afterEach(): Unit = {
//    try super.afterEach()
//    finally {
//      val run = db.run(DBIO.seq(
//        dal.graphDomains.delete
//      ))
//      Await.result(run, Duration.Inf)
//    }
//  }

}
