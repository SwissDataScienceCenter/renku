//package ch.datascience.typesystem
//
//import ch.datascience.typesystem.graphdb.GraphAccessLayer
//import org.janusgraph.core.JanusGraphFactory
//
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration.Duration
//import scala.util.{Failure, Success}
//
///**
//  * Created by johann on 21/03/17.
//  */
//object App2 {
//
//  def main(args: Array[String]): Unit = {
//
//    val graph = JanusGraphFactory.open("./conf/janusgraph-berkeleyje-es.properties")
//
//    try {
//
//      val gal = new GraphAccessLayer(graph)
//
//      val f1 = gal.run(for { pk <- gal.getPropertyKey("foo") } yield (pk.name(), pk.cardinality(), pk.dataType())).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f1, Duration.Inf)
//
//      val f2 = gal.run({mgmt => mgmt.containsPropertyKey("foo")}).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f2, Duration.Inf)
//
//      val f3 = gal.run(gal.addPropertyKey("foo", DataType.Long, Cardinality.Single)).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f3, Duration.Inf)
//
//      val f3_ = gal.run(gal.addPropertyKey("bar", DataType.String, Cardinality.Single)).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f3_, Duration.Inf)
//
//      val f4 = gal.run(for { pk1 <- gal.getPropertyKey("foo"); pk2 <- gal.getPropertyKey("bar") } yield (pk1.name(), pk2.name())).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f4, Duration.Inf)
//
//    } finally {
//      graph.close()
//    }
//
//
//  }
//
//}
