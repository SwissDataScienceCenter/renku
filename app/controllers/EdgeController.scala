package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{Keep, Source}
import ch.datascience.graph.elements.persisted.json.PersistedEdgeFormat
import ch.datascience.graph.elements.persisted.{PersistedEdge, PersistedVertex}
import org.janusgraph.graphdb.relations.RelationIdentifier
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.EdgeReader
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
import scala.util.Try

/**
  * Created by johann on 13/06/17.
  */
@Singleton
class EdgeController @Inject()(
  protected val graphExecutionContextProvider: GraphExecutionContextProvider,
  protected val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
  protected val edgeReader: EdgeReader
) extends Controller
  with JsonComponent
  with GraphTraversalComponent {

  def index: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val g = graphTraversalSource
    val t = g.E()

    val sourcePromise: Promise[Source[PersistedEdge, _]] = Promise[Source[PersistedEdge, _]]

    Future {
      graphExecutionContext.execute {
        val p = Promise[Unit]

        import scala.collection.JavaConverters._
        val s1 = Source.fromIterator(() => t.toStream.iterator().asScala)
        val s2 = s1.watchTermination()(Keep.right).mapMaterializedValue { f => f.andThen{ case _ => p.success(()) } }
        val s3 = s2.mapAsync(1)(edgeReader.read)

        sourcePromise.success(s3)

        Await.ready(p.future, Duration.Inf)
      }
    }

    sourcePromise.future.map { source =>
      val jsonSource = source.map { edge => Json.toJson(edge)(PersistedEdgeFormat) }
      val strSource = jsonSource.map(x => s"${x.toString()}\r")
      Ok.chunked(strSource).as("text/plain")
    }
  }

  def findById(id: PersistedEdge#Id): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val relId = Try { RelationIdentifier.parse(id) }

    if (relId.isFailure)
      Future.successful( NotFound )
    else {
      val g = graphTraversalSource
      val t = g.E(relId.get)

      val future: Future[Option[PersistedEdge]] = graphExecutionContext.execute {
        if (t.hasNext) {
          val edge = t.next()
          edgeReader.read(edge).map(Some.apply)
        }
        else
          Future.successful( None )
      }

      for {
        opt <- future
      } yield opt match {
        case Some(vertex) => Ok(Json.toJson(vertex)(PersistedEdgeFormat))
        case None => NotFound
      }
    }
  }

  def incident(vertexId: PersistedVertex#Id, direction: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val dir = Try {
      direction.toLowerCase match {
        case "both" | "in" | "out" => ()
      }
    }

    if (dir.isFailure)
      Future.successful( BadRequest(s"Invalid direction: $direction") )
    else {
      val g = graphTraversalSource
      val t1 = g.V(Long.box(vertexId))
      val t2 = direction.toLowerCase match {
        case "both" => t1.bothE()
        case "in" => t1.inE()
        case "out" => t1.outE()
      }

      val future: Future[Seq[PersistedEdge]] = graphExecutionContext.execute {
        import scala.collection.JavaConverters._
        val edges = t2.toList.asScala.toSeq
        Future.traverse(edges)(edgeReader.read)
      }

      for {
        edges <- future
      } yield {
        val jsonEdges = Writes.seq(PersistedEdgeFormat).writes(edges)
        Ok(JsObject(Seq("edges" -> jsonEdges)))
      }
    }
  }

}
