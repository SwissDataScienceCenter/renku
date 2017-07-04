package controllers
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.security.{KeyFactory, KeyPair}
import java.util.Base64
import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.SingleValue
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.mutation.{ImplGraphMutationClient, Mutation}
import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation}
import ch.datascience.graph.elements.new_.{NewEdge, NewVertex}
import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.StringValue
import models.{ReadResourceRequest, WriteResourceRequest}
import org.pac4j.core.profile.{CommonProfile, ProfileManager}

import scala.collection.JavaConversions._
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import models.json._
import org.apache.tinkerpop.gremlin.structure.Vertex
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.VertexReader
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller, RequestHeader}

import scala.concurrent.Future

/**
  * Created by jeberle on 25.04.17.
  */
@Singleton
class PermissionController @Inject()(config: play.api.Configuration,
                                     val playSessionStore: PlaySessionStore,
                                     wsclient: WSClient,
                                     protected val graphExecutionContextProvider: GraphExecutionContextProvider,
                                     protected val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
                                     protected val vertexReader: VertexReader
                                    ) extends Controller with JsonComponent with GraphTraversalComponent{

  lazy val host: String = config
    .getString("graph.mutation.service.host")
    .getOrElse("http://localhost:9000/api/mutation/")

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  private def getToken(implicit request: RequestHeader, claims: Map[String,AnyRef]) = {
    val public_key = Base64.getDecoder.decode(config.getString("key.resource-manager.public").get)
    val private_key = Base64.getDecoder.decode(config.getString("key.resource-manager.private").get)
    val public_spec = new X509EncodedKeySpec(public_key)
    val private_spec = new PKCS8EncodedKeySpec(private_key)
    val kf = KeyFactory.getInstance("RSA")
    val key_pair = new KeyPair(kf.generatePublic(public_spec), kf.generatePrivate(private_spec))
    val signConfig = new RSASignatureConfiguration(key_pair)
    val perm_generator = new JwtGenerator(signConfig)

    val user_jwtAuthenticator = new JwtAuthenticator()
    val user_key = Base64.getDecoder.decode(config.getString("key.keycloak.public").get)
    val user_spec = new X509EncodedKeySpec(user_key)
    val user_kf = KeyFactory.getInstance("RSA")
    val user_pair = new KeyPair(user_kf.generatePublic(user_spec), null)
    user_jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(user_pair))
    val t = user_jwtAuthenticator.validateTokenAndGetClaims(request.headers.get("Authorization").getOrElse(""))

    perm_generator.generate(t ++ claims)
  }

  private def getVertices(id: PersistedVertex#Id): Future[Map[String, PersistedVertex]] = {
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
    }).map(_.toMap)
  }


  def fileRead = Action.async(bodyParseJson[ReadResourceRequest](readResourceRequestReads)) { implicit request =>

      val profile = getProfiles(request).head
      val _request: ReadResourceRequest = request.body

      for {verticies <- getVertices(_request.resourceId)} yield {
        val  result = for {b1 <- verticies.get("bucket"); d1 <- verticies.get("data")} yield {

          val bucket = b1.properties.get("storage:bucket").head.value.unboxAs[String]
          val name = d1.properties.get("storage:filename").head.value.unboxAs[String]

              //TODO: validate its ACLs

          val token = getToken(implicitly, Map("sub" -> "StorageService", "bucket" -> bucket, "name" -> name, "scope" -> "storage:read"))

          for (appId <- _request.appId) {
            val gc = new ImplGraphMutationClient(host, implicitly, wsclient)
            val mut = Mutation(
              Seq(CreateEdgeOperation(NewEdge(
                NamespaceAndName("resource:read"),
                Right(appId),
                Right(_request.resourceId),
                Map()
              ))))
            gc.post(mut)
          }

              //TODO: check mutation result?

          Ok(Json.toJson(Map("permission_token" -> token)))
        }
        result.getOrElse(NotFound)
      }
  }

  def fileWrite = Action.async(bodyParseJson[WriteResourceRequest](writeResourceRequestReads)) { implicit request =>

      val profile = getProfiles(request).head
      val _request: WriteResourceRequest = request.body

      //TODO: validate its ACLs

      val (operation, resource_id, bucketAndName) = _request.target match {
        case Left(filename) => (Some(CreateVertexOperation(NewVertex(
          1,
          Set(NamespaceAndName("resource:file")),
          Map(
            NamespaceAndName("resource:file_name") -> SingleValue(
              DetachedRichProperty(NamespaceAndName("resource:file_name"),
                StringValue(filename),
                Map()
              )
            )
          )
        ))), Left(1), Future(Some(filename.partition(c => c == '/'))))
        case Right(id) =>
          (None, Right(id),
            for {verticies <- getVertices(id)} yield {
              for {b1 <- verticies.get("bucket"); d1 <- verticies.get("data")} yield {

                val bucket = b1.properties.get("storage:bucket").head.value.unboxAs[String]
                val name = d1.properties.get("storage:filename").head.value.unboxAs[String]
                (bucket, name)
              }
            }

          )
      }

      val edge = _request.appId.map { appId =>
        CreateEdgeOperation(NewEdge(
            NamespaceAndName("resource:write"),
            Right(appId),
            resource_id,
            Map()
          ))
      }

      val gc = new ImplGraphMutationClient(host, implicitly, wsclient)
      val mut = Mutation(operation.toSeq ++ edge.toSeq)

      for {result <- gc.post(mut); status <- gc.wait(result.uuid); bn <- bucketAndName} yield {
        bn match {
          case Some((bucket, name)) => {
            val token = getToken(implicitly, Map("sub" -> "StorageService", "bucket" -> bucket, "name" -> name, "scope" -> "storage:write"))
            Ok(Json.toJson(Map("permission_token" -> Json.toJson(token), "status" -> Json.toJson(status))))
          }
          case None => NotFound
        }

      }
  }

  def dockerExecute = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getToken(implicitly, Map("sub" -> "DeployService", "user_id" -> profile.getId, "scope" -> "compute:execute"))

      Ok(Json.toJson(Map("permission_token" -> token)))
    }
  }

}
