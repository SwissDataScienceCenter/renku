package ch.datascience.graph.types.persistence.orchestration

import ch.datascience.graph.types.persistence.DatabaseSetup
import ch.datascience.graph.types.persistence.graphdb.{GraphStack, ManagementActionRunner}
import org.janusgraph.core.{JanusGraph, JanusGraphFactory}
import org.scalatest.{AsyncTestSuite, BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 13/04/17.
  */
trait OrchestrationSetup extends DatabaseSetup with BeforeAndAfterEach with BeforeAndAfterAll { this : AsyncTestSuite =>

  //TODO: Fix thread starvation with graph (Probably ManagementActionRunner)

  lazy val jgraph: JanusGraph = JanusGraphFactory.open("src/test/resources/janusgraph-berkeleyje-es.properties")

  lazy val gdb = new ManagementActionRunner {
    override protected lazy val graph: JanusGraph = jgraph
    override protected lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  }

  lazy val gal = new GraphStack()

  lazy val ol = new OrchestrationStack(scala.concurrent.ExecutionContext.global, dbConfig, dal, gdb, gal)

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
