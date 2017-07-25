package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import authorization.ResourcesManagerJWTVerifierProvider
import backends.Backends
import ch.datascience.graph.elements.SetValue
import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.mutation.create.CreateVertexPropertyOperation
import ch.datascience.graph.elements.mutation.{GraphMutationClient, Mutation}
import ch.datascience.graph.elements.new_.NewRichProperty
import ch.datascience.graph.elements.persisted.{Path, VertexPath}
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.{LongValue, StringValue, UuidValue}
import ch.datascience.service.models.deployment.DeploymentRequest
import ch.datascience.service.models.deployment.json._
import ch.datascience.service.models.resource.{AccessGrant, ScopeQualifier}
import ch.datascience.service.security.ProfileFilterAction
import ch.datascience.service.utils.persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import ch.datascience.service.utils.persistence.reader.VertexReader
import ch.datascience.service.utils.{ControllerWithBodyParseJson, ControllerWithGraphTraversal}
import com.auth0.jwt.JWTVerifier
import models.DeployerExtrasMappers
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeployController @Inject() (
  configuration: Configuration,
  rmJwtVerifierProvider: ResourcesManagerJWTVerifierProvider,
  backends: Backends,
  implicit val graphExecutionContextProvider: GraphExecutionContextProvider,
  implicit val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
  implicit val executionContext: ExecutionContext,
  wsClient: WSClient,
  vertexReader: VertexReader
) extends Controller with ControllerWithBodyParseJson with ControllerWithGraphTraversal {

  def deploymentCreate: Action[AnyContent] = ProfileFilterAction(rmJwtVerifier).async { implicit request =>
    val accessGrant = AccessGrant(request.token.getToken)
    val token = accessGrant.verifyAccessToken(rmJwtVerifier)

    val scope = token.scope
    val (deploymentRequest, deployerId) = token.extraClaims.get.as[(DeploymentRequest, UUID)](DeployerExtrasMappers.DeployerExtrasFormat)
    println("Create request")
    println(deploymentRequest)
    val backend = deploymentRequest.deploymentType

    //TODO: check token content here.
    if (!scope.contains(ScopeQualifier.DeploymentCreate))
      Future.successful( Forbidden(s"Wring scope") )
    else {
      val g = graphTraversalSource
      val t = g.V().has("deploy:deployer_id", deployerId)

      val futureVertex = graphExecutionContext.execute {
        if (t.hasNext) {
          vertexReader.read(t.next()).map(Some.apply)
        }
        else
          Future.successful(None)
      }

      futureVertex.flatMap { optVertex =>
        optVertex.map { vertex =>
          val additionalEnv: Map[String, String] = Map(
            "SDSC_EXECUTION_ID" -> s"${vertex.id}",
            "SDSC_ACCESS_TOKEN" -> accessGrant.accessToken
          )

          backends.getBackend(backend) match {
            case Some(b) => b.create(request.userId, deploymentRequest, additionalEnv).flatMap { backendId =>
              val statusProp = NewRichProperty(
                VertexPath(vertex.id),
                NamespaceAndName("deploy:status"),
                StringValue("created"),
                Map(NamespaceAndName("system:creation_time") -> DetachedProperty(NamespaceAndName("system:creation_time"), LongValue(System.currentTimeMillis)))
              )
              val backendIdProp = NewRichProperty(
                VertexPath(vertex.id),
                NamespaceAndName("deploy:backend_id"),
                StringValue(backendId),
                Map.empty
              )

              val mut: Mutation = Mutation(Seq(
                CreateVertexPropertyOperation(statusProp),
                CreateVertexPropertyOperation(backendIdProp)
              ))

              graphMutationClient.post(mut).map { event =>
                //TODO: maybe take into account if the node was created or not
                Ok(s"Deployed: ${vertex.id}")
              }
            }
            case None => Future.successful(BadRequest(s"The backend $backend is not enabled."))
          }
        }.getOrElse(Future.successful(NotFound("Deployment not found")))
      }
    }
  }

  protected lazy val rmJwtVerifier: JWTVerifier = rmJwtVerifierProvider.get

  protected lazy val graphMutationClient: GraphMutationClient = GraphMutationClient(
    configuration.getString("graph.mutation.service.host").get,
    executionContext,
    wsClient
  )

}
