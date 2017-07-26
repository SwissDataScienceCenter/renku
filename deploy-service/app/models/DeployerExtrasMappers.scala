package models

import java.util.UUID

import ch.datascience.service.models.deployment.DeploymentRequest
import ch.datascience.service.models.deployment.json._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object DeployerExtrasMappers {

  def DeployerExtrasFormat: OFormat[(DeploymentRequest, UUID, String)] = (
    (JsPath \ "request").format[DeploymentRequest] and
      (JsPath \ "deployer_id").format[UUID] and
      (JsPath \ "access_token").format[String]
  ) (Tuple3.apply, unlift(Tuple3.unapply))

}
