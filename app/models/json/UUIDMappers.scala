package models.json

import java.util.UUID

import play.api.libs.json._

/**
  * Created by johann on 13/04/17.
  */
object UUIDMappers {

  def uuidWrites: Writes[UUID] = new Writes[UUID] {
    def writes(uuid: UUID): JsString = JsString(uuid.toString)
  }

  def uuidReads: Reads[UUID] = new Reads[UUID] {
    def reads(json: JsValue): JsResult[UUID] = json.validate[String] flatMap { str =>
      try {
        JsSuccess(UUID.fromString(str))
      } catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }
  }

  def notUUidReads: Reads[String] = new Reads[String] {
    def reads(json: JsValue): JsResult[String] = json.validate[UUID] match {
      case JsSuccess(uuid, _) => JsError(s"UUID string forbidden: $uuid")
      case JsError(_) => json.validate[String]
    }
  }

}
