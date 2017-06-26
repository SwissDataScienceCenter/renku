package models.json

import models.DeployResult
import play.api.libs.json._
import play.api.libs.functional.syntax._

object DeployResultMappers {

  def deployResultWrite: Writes[DeployResult] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "success").write[Boolean] and
    (JsPath \ "ports-forwarding").write[Map[String, String]] and
    (JsPath \ "images").write[List[String]] and
    (JsPath \ "message").write[String]
  )(unlift(DeployResult.unapply))

}
