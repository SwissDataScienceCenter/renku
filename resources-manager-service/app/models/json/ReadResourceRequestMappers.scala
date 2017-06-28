package models.json

import models.ReadResourceRequest
import play.api.libs.json._
import play.api.libs.functional.syntax._


object ReadResourceRequestMappers {

  def readResourceRequestReads: Reads[ReadResourceRequest] = (
      (JsPath \ "app_id").readNullable[Long] and
      (JsPath \ "resource_id").read[Long]
    )(ReadResourceRequest.apply _)
}
