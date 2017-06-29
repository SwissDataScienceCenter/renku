package models.json

import models.DeployRequest
import play.api.libs.json._
import play.api.libs.functional.syntax._


object DeployRequestMappers {

  def deployRequestReads: Reads[DeployRequest] = (
      (JsPath \ "app-id").readNullable[Long] and
      (JsPath \ "id").read[String] and
      (JsPath \ "docker_image").read[String] and
      (JsPath \ "network_port").readNullable[List[Int]] and
      (JsPath \ "env").readNullable[Map[String, String]]
    )(DeployRequest.apply _)
}
