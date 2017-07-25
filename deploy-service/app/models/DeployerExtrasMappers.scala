package models

import java.util.UUID

import ch.datascience.service.models.deployment.DeploymentRequest
import ch.datascience.service.models.deployment.json._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object DeployerExtrasMappers {

  def DeployerExtrasFormat: OFormat[(DeploymentRequest, UUID)] = (
    (JsPath \ "request").format[DeploymentRequest] and
      (JsPath \ "deployer_id").format[UUID]
  ) (Tuple2.apply, unlift(Tuple2.unapply))

}
