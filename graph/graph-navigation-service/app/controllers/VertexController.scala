package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{Keep, Source}
import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.elements.persisted.json.PersistedVertexFormat
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.VertexReader
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

/**
  * Created by johann on 13/06/17.
  */
@Singleton
class VertexController @Inject()(
  protected val graphExecutionContextProvider: GraphExecutionContextProvider,
  protected val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
  protected val vertexReader: VertexReader
) extends Controller
  with JsonComponent
  with GraphTraversalComponent {

  def index: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val g = graphTraversalSource
    val t = g.V()

    val sourcePromise: Promise[Source[PersistedVertex, _]] = Promise[Source[PersistedVertex, _]]

    Future {
      graphExecutionContext.execute {
        val p = Promise[Unit]

        import scala.collection.JavaConverters._
        val s1 = Source.fromIterator(() => t.toStream.iterator().asScala)
        val s2 = s1.watchTermination()(Keep.right).mapMaterializedValue { f => f.andThen{ case _ => p.success(()) } }
        val s3 = s2.mapAsync(1)(vertexReader.read)

        sourcePromise.success(s3)

        Await.ready(p.future, Duration.Inf)
      }
    }

    sourcePromise.future.map { source =>
      val jsonSource = source.map { vertex => Json.toJson(vertex)(PersistedVertexFormat) }
      val strSource = jsonSource.map(x => s"${x.toString()}\r")
      Ok.chunked(strSource).as("text/plain")
    }
  }

  def findById(id: PersistedVertex#Id): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val g = graphTraversalSource
    val t = g.V(Long.box(id))

    val future: Future[Option[PersistedVertex]] = graphExecutionContext.execute {
      if (t.hasNext) {
        val vertex = t.next()
        vertexReader.read(vertex).map(Some.apply)
      }
      else
        Future.successful( None )
    }

    for {
      opt <- future
    } yield opt match {
      case Some(vertex) => Ok(Json.toJson(vertex)(PersistedVertexFormat))
      case None => NotFound
    }

  }

}
