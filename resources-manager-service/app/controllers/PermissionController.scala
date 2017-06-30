package controllers
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.security.{KeyFactory, KeyPair}
import java.util.Base64
import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.SingleValue
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation}
import ch.datascience.graph.elements.new_.{NewEdge, NewVertex}
import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.elements.persisted.json.PersistedVertexFormat
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.StringValue
import clients.GraphClient
import models.{ReadResourceRequest, WriteResourceRequest}
import org.pac4j.core.profile.{CommonProfile, ProfileManager}

import scala.collection.JavaConversions._
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import models.json._
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.VertexReader

import scala.concurrent.duration._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller, RequestHeader}

import scala.concurrent.{Await, Future}

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

  implicit val ws: WSClient = wsclient
  implicit lazy val host: String = config
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

  private def getVertices(id: PersistedVertex#Id): scala.collection.Map[String, Future[PersistedVertex]] = {
    val g = graphTraversalSource
    val t = g.V(Long.box(id)).as("data").outE("resource:stored_in").outV().as("bucket").select("data", "bucket")

    graphExecutionContext.execute {
      import scala.collection.JavaConversions._
      if (t.hasNext)
        t.next().mapValues(vertexReader.read(_))
      else
        Map()
    }
  }


  def authorizeStorageRead = Action.async(bodyParseJson[ReadResourceRequest](readResourceRequestReads)) { implicit request =>

      val profile = getProfiles(request).head
      val _request: ReadResourceRequest = request.body

      val verticies = getVertices(_request.resourceId)
      verticies.get("bucket").flatMap(
        b1 => verticies.get("data").map(d1 =>
        b1.flatMap( b2 => d1.map( d2 => {
          val b3 = b2.properties.get("storage:bucket").head.value.unboxAs[String]
          val d3 = d2.properties.get("storage:filename").head.value.unboxAs[String]

          //TODO: validate its ACLs

          val token = getToken(implicitly, Map("sub" -> "StorageService", "bucket" -> b3, "name" -> d3, "scope" -> "storage:read"))

          for (appId <- _request.appId) {
            val gc = new GraphClient
            val did = NamespaceAndName("deploy:id")
            val dimage = NamespaceAndName("deploy:image")
            val dstatus = NamespaceAndName("deploy:status")
            val dtime = NamespaceAndName("system:creation_time")
            val mut = Mutation(
              Seq(CreateEdgeOperation(NewEdge(
                NamespaceAndName("resource:read"),
                Right(appId),
                Right(_request.resourceId),
                Map()
              ))))
            gc.create(mut)
          }

          //TODO: check mutation result

          Ok("{\"permission_token\": \"" + token + "\"}")
        }
        )))).getOrElse(Future(NotFound))

  }

  def authorizeStorageWrite = Action.async(bodyParseJson[WriteResourceRequest](writeResourceRequestReads)) { implicit request =>

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
        ))), Left(1), Future(filename.partition(c => c == '/')))
        case Right(id) =>
          val verticies = getVertices(id)
          (None, Right(id),
          verticies.get("bucket").flatMap(
            b1 => verticies.get("data").map(d1 =>
              b1.flatMap( b2 => d1.map( d2 => {
                val b3 = b2.properties.get("storage:bucket").head.value.unboxAs[String]
                val d3 = d2.properties.get("storage:filename").head.value.unboxAs[String]
                (b3, d3)
              })))).getOrElse(Future(("","")))

                 )
      }

      val edge = _request.appId.map { appId =>

        val did = NamespaceAndName("deploy:id")
        val dimage = NamespaceAndName("deploy:image")
        val dstatus = NamespaceAndName("deploy:status")
        val dtime = NamespaceAndName("system:creation_time")
        CreateEdgeOperation(NewEdge(
            NamespaceAndName("resource:write"),
            Right(appId),
            resource_id,
            Map()
          ))
      }

      val gc = new GraphClient
      val mut = Mutation(operation.toSeq ++ edge.toSeq)

      def getVertexId(result: JsValue): Long = {
        Thread.sleep(1000)
        val status = gc.status((result \ "uuid").as[String])
        val s = Await.result(status, 5.seconds)
        if ((s \ "status").as[String].equals("completed"))
          (s \ "response" \ "event" \ "results" \ 0 \ "id").as[Long]
        else
          getVertexId(result)
      }

      gc.create(mut).map(result => {
        val res_id = _request.target.fold(_ => getVertexId(result), id => id)
        val token = getToken(implicitly, Map("sub" -> "StorageService", "user_id" -> profile.getId, "file_uuid" -> res_id.toString, "scope" -> "storage:write"))

        Ok("{\"permission_token\": \"" + token + "\", \"id\": " + res_id + " }")
      })
  }

  def authorizeComputeExecute = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getToken(implicitly, Map("sub" -> "DeployService", "user_id" -> profile.getId, "scope" -> "compute:execute"))

      Ok("{\"permission_token\": \"" + token + "\"}")
    }
  }

}
