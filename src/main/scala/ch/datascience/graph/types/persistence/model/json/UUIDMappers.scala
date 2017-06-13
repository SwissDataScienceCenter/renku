package ch.datascience.graph.types.persistence.model.json

import java.util.UUID

import play.api.libs.json._

/**
  * Created by johann on 13/04/17.
  */
object UUIDMappers {

  lazy val UUIDFormat: Format[UUID] = Format(uuidReads, uuidWrites)

  private[this] def uuidWrites: Writes[UUID] = new Writes[UUID] {
    def writes(uuid: UUID): JsString = JsString(uuid.toString)
  }

  private[this] def uuidReads: Reads[UUID] = new Reads[UUID] {
    def reads(json: JsValue): JsResult[UUID] = json.validate[String] flatMap { str =>
      try {
        JsSuccess(UUID.fromString(str))
      } catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }
  }

  lazy val notUUidReads: Reads[String] = new Reads[String] {
    def reads(json: JsValue): JsResult[String] = json.validate[UUID] match {
      case JsSuccess(uuid, _) => JsError(s"UUID string forbidden: $uuid")
      case JsError(_) => json.validate[String]
    }
  }

}
