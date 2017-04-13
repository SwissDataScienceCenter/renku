//package ch.datascience.typesystem.orchestration
//
//import ch.datascience.typesystem.graphdb.GraphAccessLayer
//import ch.datascience.typesystem.model.table.DataAccessLayer
//import org.janusgraph.core.JanusGraph
//import slick.basic.DatabaseConfig
//import slick.jdbc.JdbcBackend.Database
//import slick.jdbc.JdbcProfile
//
///**
//  * Created by johann on 04/04/17.
//  */
//class Orchestrator(val db: Database, val dal: DataAccessLayer, val gal: GraphAccessLayer) extends DatabaseComponent with GraphComponent with GraphDomainOrchestrator with PropertyKeyOrchestrator  {
//}
//
//object Orchestrator {
//
//  def apply(dbConfig: DatabaseConfig[_ <: JdbcProfile], graph: JanusGraph): Orchestrator = {
//    val db = Database.forConfig("", config = dbConfig.config)
//    val dal = new DataAccessLayer(dbConfig.profile)
//
//    val gal = new GraphAccessLayer(graph)
//
//    new Orchestrator(db, dal, gal)
//  }
//
//}
