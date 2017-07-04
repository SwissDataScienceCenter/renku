package controllers

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.Constants
import ch.datascience.graph.elements.SingleValue
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.mutation.{GraphMutationClient, ImplGraphMutationClient, Mutation}
import ch.datascience.graph.elements.mutation.create.CreateVertexOperation
import ch.datascience.graph.elements.new_.json.NewVertexFormat
import ch.datascience.graph.elements.new_.NewVertex
import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.elements.persisted.json.PersistedVertexFormat
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.StringValue
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.VertexReader
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import models.json._
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller, RequestHeader}

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent.Future

/**
  * Created by jeberle on 25.04.17.
  */
@Singleton
class ExplorerController @Inject()(config: play.api.Configuration,
                                   val playSessionStore: PlaySessionStore,
                                   wsclient: WSClient,
                                   protected val graphExecutionContextProvider: GraphExecutionContextProvider,
                                   protected val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
                                   protected val vertexReader: VertexReader
                                    ) extends Controller with JsonComponent with GraphTraversalComponent{

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  private def getVertex(id: PersistedVertex#Id): Future[Option[PersistedVertex]] = {
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
    future
  }

  def bucketBackends = Action.async { implicit request =>
    Future(Ok(Json.toJson(List("swift"))))
  }


  def bucketList = Action.async { implicit request =>
    val g = graphTraversalSource
    val t = g.V().has(Constants.TypeKey, "resource:bucket")

    val future: Future[Seq[PersistedVertex]] = graphExecutionContext.execute {
      Future.sequence(t.toIterable.map(v =>
        vertexReader.read(v)
      ).toSeq)
    }
    future.map(s => Ok(Json.toJson(s)))

  }

  def fileList(id: Long) = Action.async { implicit request =>
    val g = graphTraversalSource
    val t = g.V(Long.box(id)).inE("resource:stored_in").inV().has(Constants.TypeKey, "resource:file")

    val future: Future[Seq[PersistedVertex]] = graphExecutionContext.execute {
      Future.sequence(t.toIterable.map(v =>
        vertexReader.read(v)
      ).toSeq)
    }
    future.map(s => Ok(Json.toJson(s)))

  }

  def fileMetadata(id: Long) = Action.async { implicit request =>

    val g = graphTraversalSource
    val t = g.V(Long.box(id)).as("data").out("resource:stored_in").as("bucket").select[Vertex]("data", "bucket")

    Future.sequence(graphExecutionContext.execute {
      import collection.JavaConverters._
      if (t.hasNext) {
        val jmap: Map[String, Vertex] = t.next().asScala.toMap
        for {
          (key, value) <- jmap
        } yield for {
          vertex <- vertexReader.read(value)
        } yield key -> vertex
      }
      else
        Seq.empty
    }).map(i => Ok(Json.toJson(i.toMap)))
  }

  def bucketMetadata(id: Long) = Action.async { implicit request =>

    getVertex(id).map {
      case Some(vertex) =>
        if (vertex.types.contains(NamespaceAndName("resource:bucket")))
          Ok(Json.toJson(vertex)(PersistedVertexFormat))
        else
          NotAcceptable // to differentiate from not found
      case None => NotFound
    }
  }

  def bucketCreate = Action.async(bodyParseJson[NewVertex](NewVertexFormat)) {
    implicit request =>
      val profile = getProfiles(request).head
      val bucketVertex: NewVertex = request.body
      val v = NewVertex(bucketVertex.tempId, bucketVertex.types, bucketVertex.properties +
        (NamespaceAndName("system:owner") -> SingleValue(
        DetachedRichProperty(NamespaceAndName("system:owner"),
          StringValue(profile.getEmail),
          Map()))))
      bucketVertex.properties
      val gc = GraphMutationClient(config
        .getString("graph.mutation.service.host")
        .getOrElse("http://localhost:9000/api/mutation/"),implicitly, wsclient)
      val mut = Mutation(Seq(CreateVertexOperation(v)))
      gc.post(mut).flatMap(e => gc.wait(e.uuid).map(s => Ok(Json.toJson(s))))
  }

}
