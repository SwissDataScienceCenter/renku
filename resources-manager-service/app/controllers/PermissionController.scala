package controllers

import java.util.UUID

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.SingleValue
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.mutation.{ImplGraphMutationClient, Mutation}
import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation}
import ch.datascience.graph.elements.new_.{NewEdge, NewVertex}
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.StringValue
import ch.datascience.service.models.resources.ResourceRequest

import scala.collection.JavaConversions._
import org.pac4j.play.store.PlaySessionStore
import ch.datascience.service.models.resources.json._
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import persistence.reader.VertexReader
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.Future

/**
  * Created by jeberle on 25.04.17.
  */
@Singleton
class PermissionController @Inject()(implicit val config: play.api.Configuration,
                                     implicit val playSessionStore: PlaySessionStore,
                                     wsclient: WSClient,
                                     implicit val graphExecutionContextProvider: GraphExecutionContextProvider,
                                     implicit val janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider,
                                     implicit val vertexReader: VertexReader
                                    ) extends Controller with JsonComponent with GraphTraversalComponent with RequestHelper {

  lazy val host: String = config
    .getString("graph.mutation.service.host")
    .getOrElse("http://localhost:9000/api/mutation/")


  def authorize = Action.async(bodyParseJson[ResourceRequest](resourceRequestFormat)) { implicit request =>

      val profile = getProfiles().head
      val _request: ResourceRequest = request.body

      for {verticies <- getVertices(_request.resourceId)} yield {
        val  result = for {b1 <- verticies.get("bucket"); d1 <- verticies.get("data")} yield {

//          val bucket = b1.properties.get("resource:bucket_name").head.value.unboxAs[String]
          val bucket = b1.properties.get("resource:bucket_backend_id").head.value.unboxAs[UUID].toString
          val name = d1.properties.get("resource:file_name").head.value.unboxAs[String]

              //TODO: validate its ACLs

          val token = getToken(Map("sub" -> "StorageService", "bucket" -> bucket, "name" -> name, "scope" -> "storage:read"))

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
/*
  def fileWrite = Action.async(bodyParseJson[WriteResourceRequest](writeResourceRequestReads)) { implicit request =>

      val profile = getProfiles().head
      val _request: WriteResourceRequest = request.body

      //TODO: validate its ACLs

    (_request.target match {
        case Left(filename) =>
          getVertex(_request.bucket).map {
            case Some(vertex) =>
              if (vertex.types.contains(NamespaceAndName("resource:bucket"))) {
                (List(
                  CreateVertexOperation(NewVertex(
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
                  )),
                  CreateEdgeOperation(NewEdge(
                    NamespaceAndName("resource:stored_in"),
                    Right(_request.bucket),
                    Left(1),
                    Map()
                  ))
//                ), Left(1), Option(vertex.properties.get("resource:bucket_name").head.value.unboxAs[String], filename))
                ), Left(1), Option(vertex.properties.get("resource:bucket_backend_id").head.value.unboxAs[UUID].toString, filename))
              }
              else (List(), Left(0), None)
            case None => (List(), Left(0), None)
          }
        case Right(id) =>
          for {verticies <- getVertices(id)} yield {
            (List(), Right(id),
              for {b1 <- verticies.get("bucket"); d1 <- verticies.get("data")}
//                yield (b1.properties.get("resource:bucket_name").head.value.unboxAs[String],
                yield (b1.properties.get("resource:bucket_backend_id").head.value.unboxAs[UUID].toString,
                  d1.properties.get("resource:file_name").head.value.unboxAs[String])
            )
            }

      }).flatMap {
      case (operations, resource_id, Some((bucket, name))) => {
        val edge = _request.appId.map { appId =>
          CreateEdgeOperation(NewEdge(
            NamespaceAndName("resource:write"),
            Right(appId),
            resource_id,
            Map()
          ))
        }

        val gc = new ImplGraphMutationClient(host, implicitly, wsclient)
        val mut = Mutation(operations ++ edge.toSeq)

        for {result <- gc.post(mut); status <- gc.wait(result.uuid)} yield {
          val token = getToken(Map("sub" -> "StorageService", "bucket" -> bucket, "name" -> name, "scope" -> "storage:write"))
          Ok(Json.toJson(Map("permission_token" -> Json.toJson(token), "status" -> Json.toJson(status))))
        }
      }
    case (operations, resource_id, None) => Future(NotFound)
    }
  }

  def dockerExecute = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles().head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getToken(Map("sub" -> "DeployService", "user_id" -> profile.getId, "scope" -> "compute:execute"))

      Ok(Json.toJson(Map("permission_token" -> token)))
    }
  }*/

}
