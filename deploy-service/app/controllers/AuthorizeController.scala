package controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }

import authorization.{ JWTVerifierProvider, ResourcesManagerJWTVerifierProvider }
import backends.Backends
import ch.datascience.graph.elements.mutation.{ GraphMutationClient, Mutation }
import ch.datascience.graph.elements.mutation.create.{ CreateEdgeOperation, CreateVertexOperation }
import ch.datascience.graph.elements.mutation.log.model.EventStatus
import ch.datascience.graph.elements.new_.NewEdge
import ch.datascience.graph.elements.new_.build.NewVertexBuilder
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.{ LongValue, StringValue, UuidValue }
import ch.datascience.service.ResourceManagerClient
import ch.datascience.service.models.deployment.DeploymentRequest
import ch.datascience.service.models.deployment.json._
import ch.datascience.service.models.resource.{ AccessGrant, AccessRequest }
import ch.datascience.service.models.resource.json._
import ch.datascience.service.security.ProfileFilterAction
import ch.datascience.service.utils.persistence.graph.{ GraphExecutionContextProvider, JanusGraphTraversalSourceProvider }
import ch.datascience.service.utils.{ ControllerWithBodyParseJson, ControllerWithGraphTraversal }
import com.auth0.jwt.JWTVerifier
import models.DeployerExtrasMappers
import play.api.Configuration
import play.api.libs.json.{ JsObject, JsPath, Json }
import play.api.libs.ws.WSClient
import play.api.libs.functional.syntax._
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class AuthorizeController @Inject() (
    configuration:                                  Configuration,
    jwtVerifierProvider:                            JWTVerifierProvider,
    rmJwtVerifierProvider:                          ResourcesManagerJWTVerifierProvider,
    resourceManagerClient:                          ResourceManagerClient,
    implicit val graphExecutionContextProvider:     GraphExecutionContextProvider,
    implicit val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
    implicit val executionContext:                  ExecutionContext,
    wsClient:                                       WSClient
) extends Controller with ControllerWithBodyParseJson with ControllerWithGraphTraversal {

  def authorizeDeploymentCreate: Action[DeploymentRequest] = ProfileFilterAction( jwtVerifier ).async( bodyParseJson[DeploymentRequest] ) { implicit request =>
    /* Steps:
     *   1. Request authorization from Resource Manager
     *   2. Validate response from RM
     *   3. Log to KnowledgeGraph
     */

    // Step 0: Generate deployer id
    //"deploy:deployer_id"
    val deployerId: UUID = UUID.randomUUID()

    // Step 1: Request authorization from Resource Manager
    //    val extra: JsObject = Json.toJson(request.body).as[JsObject]
    val strToken: String = request.token.getToken
    val extra: JsObject = Json.toJson( ( request.body, deployerId, strToken ) )( DeployerExtrasMappers.DeployerExtrasFormat ).as[JsObject]
    val accessRequest: AccessRequest = request.executionId match {
      case Some( execId ) => request.body.withParent( execId ).toAccessRequest( Some( extra ) ) // We include the execution_id if this is a subprocess deployment
      case None           => request.body.toAccessRequest( Some( extra ) )
    }
    val futureGrant: Future[Option[AccessGrant]] = resourceManagerClient.authorize( AccessRequestFormat, accessRequest, s"Bearer $strToken" )

    // Step 2: Validate response from RM
    futureGrant.flatMap { optionAccessGrant =>
      optionAccessGrant.map { accessGrant =>
        if ( accessGrant.verifyAccessToken( rmJwtVerifier ).extraClaims.contains( extra ) ) {
          // Step 3: Log to KnowledgeGraph
          val vertexBuilder = new NewVertexBuilder()
            .addType( NamespaceAndName( "deploy:deployment" ) )
            .addSingleProperty( "deploy:deployer_id", UuidValue( deployerId ) )
            .addSingleProperty( "deploy:description", StringValue( extra.toString() ) )
            .addSetProperty( NamespaceAndName( "deploy:status" ), StringValue( "authorized" ), _.addProperty( NamespaceAndName( "system:creation_time" ), LongValue( System.currentTimeMillis ) ) )
          val newVertex = vertexBuilder.result()
          val edgeOperation = request.executionId.map { execId =>
            CreateEdgeOperation( NewEdge(
              NamespaceAndName( "deploy:subprocess" ),
              Right( execId ),
              Left( newVertex.tempId ),
              Map()
            ) )
          }
          val mut = Mutation(
            Seq( CreateVertexOperation( newVertex ) ) ++ edgeOperation.toSeq
          )
          graphMutationClient.post( mut ).map { event =>
            //TODO: maybe take into account if the node was created or not
            Ok( Json.toJson( accessGrant ) )
          }
        }
        else
          Future( InternalServerError( "Resource Manager response is invalid." ) )
      }.getOrElse( Future( InternalServerError( "No response from Resource Manager." ) ) )
    }
  }

  protected lazy val jwtVerifier: JWTVerifier = jwtVerifierProvider.get

  protected lazy val rmJwtVerifier: JWTVerifier = rmJwtVerifierProvider.get

  protected lazy val graphMutationClient: GraphMutationClient = GraphMutationClient(
    configuration.getString( "graph.mutation.service.host" ).get,
    executionContext,
    wsClient
  )

}
