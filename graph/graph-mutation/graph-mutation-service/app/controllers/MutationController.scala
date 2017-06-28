package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.json.MutationFormat
import ch.datascience.graph.elements.mutation.log.model.Event
import models.{RequestDAO, RequestWorker, ResponseWorker}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class MutationController @Inject()(
  protected val requestWorker: RequestWorker,
  protected val responseWorker: ResponseWorker,
  protected val dao: RequestDAO
) extends Controller
  with JsonComponent {

  implicit lazy val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  implicit lazy val mutationFormat: Format[Mutation] = MutationFormat

  def postMutation: Action[Mutation] = Action.async(bodyParseJson[Mutation](MutationFormat)) { implicit request =>
    val query = request.body
    //TODO: get token from header
    val token: JsValue = Json.parse("""{ "my-token-is-empty": true }""")
    val event = JsObject(Seq(
      "query" -> Json.toJson(query),
      "token" -> token
    ))

    for {
      event <- requestWorker.add(event)
    } yield {
//      val json = JsObject(Seq(
//        "uuid" -> JsString(event.uuid.toString),
//        "event" -> event.event,
//        "timestamp" -> JsString(event.created.atZone(java.time.ZoneId.of("UTC")).toString)
//      ))
//      Ok(json)
      Ok(Json.toJson(event))
    }
  }

  def findById(id: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val json = JsString(id)
    json.validate[UUID] match {
      case JsError(e) => Future.successful(BadRequest(JsError.toJson(e)))
      case JsSuccess(uuid, _) =>
        val future = dao.findByIdWithResponse(uuid)
        future map {
          case Some((req, Some(res))) =>
            Ok(JsObject(Seq("request" -> Json.toJson(req), "response" -> Json.toJson(res), "status" -> JsString("completed"))))
          case Some((req, None)) =>
            Ok(JsObject(Seq("request" -> Json.toJson(req), "status" -> JsString("pending"))))
          case None => NotFound
        }
    }
  }

  private[this] implicit lazy val eventWrites: Writes[Event] = (
    (JsPath \ "uuid").write[String] and
      (JsPath \ "event").write[JsValue] and
      (JsPath \ "timestamp").write[String]
  ){ event => (event.uuid.toString, event.event, event.created.atZone(java.time.ZoneId.of("UTC")).toString) }

}
