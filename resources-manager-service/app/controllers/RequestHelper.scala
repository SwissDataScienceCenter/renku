package controllers

import java.security.{KeyFactory, KeyPair}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.util.Base64

import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.execution.GraphExecutionContext
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import persistence.reader.VertexReader
import play.api.mvc.{Controller, RequestHeader}
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.Future

/**
  * Created by jeberle on 04.07.17.
  */
trait RequestHelper { this: Controller =>

  def getProfiles()(implicit request: RequestHeader, playSessionStore: PlaySessionStore): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  def getToken(claims: Map[String, AnyRef])(implicit request: RequestHeader, config: play.api.Configuration) = {
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
    t.putAll(claims.asJava)
    perm_generator.generate(t)
  }

  def getVertices(id: PersistedVertex#Id)(implicit graphExecutionContext: GraphExecutionContext,
                  graphTraversalSource: GraphTraversalSource, vertexReader: VertexReader
                 ): Future[Map[String, PersistedVertex]] = {
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


  def getVertex(id: PersistedVertex#Id)(implicit graphExecutionContext: GraphExecutionContext,
                graphTraversalSource: GraphTraversalSource, vertexReader: VertexReader
                ): Future[Option[PersistedVertex]] = {
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

}
