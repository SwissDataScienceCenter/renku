package init

import ch.datascience.graph.elements.tinkerpop_mappers.VertexPropertyReader
import org.apache.tinkerpop.gremlin.structure.VertexProperty
//import ch.datascience.graph.elements.mappers._
//import ch.datascience.graph.elements.mappers.tinkerpop.{BoxedReader, PersistedVertexReader, Reader, StringReader}
import ch.datascience.graph.scope.Scope
import ch.datascience.graph.scope.persistence.relationaldb.RelationalPersistenceLayer
import ch.datascience.graph.types.persistence.graphdb.{GraphStack, ManagementActionRunner}
import ch.datascience.graph.types.persistence.orchestration.OrchestrationStack
import ch.datascience.graph.types.persistence.relationaldb.DatabaseStack
import org.janusgraph.core.{JanusGraph, JanusGraphFactory}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by johann on 22/05/17.
  */
object TestGraphRead {

  def main(args: Array[String]): Unit = {

    val ec: ExecutionContext = implicitly[ExecutionContext]

    val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.sqldb")

    val graph: JanusGraph = JanusGraphFactory.open("/home/johann/projects/sdsc/graph-all/graph-typesystem/graph-typesystem-service/conf/janusgraph-cassandra.properties")

    val gdb = new ManagementActionRunner {
      protected def graph: JanusGraph = JanusGraphFactory.open("conf/janusgraph-cassandra.properties")

      protected def ec: ExecutionContext = implicitly[ExecutionContext]
    }

    val dal = new DatabaseStack(dbConfig)
    val gal = new GraphStack()

    val orchestrationStack = new OrchestrationStack(ec, dbConfig, dal, gdb, gal)

    val pl = new RelationalPersistenceLayer(ec, orchestrationStack)

    // Clean graph
    graph.traversal().E().drop().iterate()
    graph.traversal().V().drop().iterate()
    graph.traversal().tx().commit()

    /* Use rest api to define:
    {
        "namespace": "foo"
        "name": "bar",
        "dataType": "long",
        "cardinality": "single"
    },
    {
        "namespace": "foo"
        "name": "baz",
        "dataType": "string",
        "cardinality": "list"
     }
     */

    // Add a vertex
    val t = graph.traversal()
    val tx = t.tx()
    t.addV().property("foo:bar", 120, "foo:baz", "hello, meta").property("foo:baz", "a").property("foo:baz", "b", "foo:baz", "hello again, meta").iterate()
    tx.commit()

    println(t.V().toList.asScala.toList)

    val scope = new Scope(pl)

//    implicit val keyReader = new StringReader[NamespaceAndName] {
//      def read(x: String)(implicit ec: ExecutionContext): Future[NamespaceAndName] = Future.successful( NamespaceAndName(x) )
//    }
//
//    implicit val idReader = new Reader[java.lang.Object, Long] {
//      def read(x: Object)(implicit ec: ExecutionContext): Future[Long] = Future.successful( x.asInstanceOf[Long] )
//    }
//
//    implicit val propIdReader = new Reader[java.lang.Object, org.janusgraph.graphdb.relations.RelationIdentifier] {
//      def read(x: Object)(implicit ec: ExecutionContext): Future[RelationIdentifier] = Future.successful( x.asInstanceOf[org.janusgraph.graphdb.relations.RelationIdentifier] )
//    }
//
//    implicit val boxReader = new BoxedReader[NamespaceAndName](scope)
//

//    val reader = VertexReader(scope)
//    graph.tx().rollback()
//    val vertices = t.V().toList.asScala.toList
//    val mappedVertices = for {
//      vertex <- vertices
//    } yield reader.read(vertex)
//    graph.tx().commit()
//    graph.close()
//
//    for (future <- mappedVertices) {
//      future.map { vertex =>
//        println(vertex)
//        for (y <- vertex.properties.values; z <- y) {
//          println(z.key)
//          println(z.id)
//          println(z.value)
//          println(z.properties)
//        }
//      }
//      future.onFailure {
//        case e => e.printStackTrace()
//      }
//    }

    val reader2 = VertexPropertyReader(scope)
    graph.tx().rollback()
    val props = t.V().properties().toList.asScala.toList
    val mapped = for {
      prop <- props
    } yield reader2.read(prop.asInstanceOf[VertexProperty[java.lang.Object]])
    graph.tx().commit()
    graph.close()

    for (future <- mapped) {
      future.map { prop =>
        println(prop)
      }
      future.onFailure {
        case e => e.printStackTrace()
      }
    }

  }


}
