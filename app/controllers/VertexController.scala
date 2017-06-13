package controllers

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.detached.json.DetachedVertexFormat
import ch.datascience.graph.elements.detached.{DetachedProperty, DetachedRichProperty, DetachedVertex}
import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.elements.{ListValue, SetValue, SingleValue}
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.VertexReader
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc._

import scala.concurrent.Future

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
      case Some(vertex) => Ok(Json.toJson(vertex)(vertexWrites))
      case None => NotFound
    }

  }

  private[this] lazy val vertexWrites: Writes[PersistedVertex] = (
    (JsPath \ "id").write[PersistedVertex#Id] and
      JsPath.write[DetachedVertex](DetachedVertexFormat)
    ){ vertex =>
    val props = vertex.properties.mapValues{
      case SingleValue(p) => SingleValue(DetachedRichProperty(p.key, p.value, p.properties.mapValues(mp => DetachedProperty(mp.key, mp.value))))
      case SetValue(ps) => SetValue(for { p <- ps} yield DetachedRichProperty(p.key, p.value, p.properties.mapValues(mp => DetachedProperty(mp.key, mp.value))))
      case ListValue(ps) => ListValue(for {p <- ps} yield DetachedRichProperty(p.key, p.value, p.properties.mapValues(mp => DetachedProperty(mp.key, mp.value))))
    }
    (vertex.id, DetachedVertex(vertex.types, props))
  }

}
