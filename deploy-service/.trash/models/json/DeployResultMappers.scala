package models.json

import models.{DeployResult, JobStatus, PodStatus}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object DeployResultMappers {

  def podStatusWrite: Writes[PodStatus] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "phase").write[String] and
      (JsPath \ "reason").writeNullable[String] and
      (JsPath \ "message").writeNullable[String]
    )(unlift(PodStatus.unapply))

  implicit lazy val _podStatusWrite: Writes[PodStatus] = podStatusWrite


  def jobStatusWrite: Writes[JobStatus] = (
    (JsPath \ "start").writeNullable[String] and
      (JsPath \ "complete").writeNullable[String] and
      (JsPath \ "succeeded").writeNullable[Int] and
      (JsPath \ "failed").writeNullable[Int] and
      (JsPath \ "pods").write[List[PodStatus]]
    )(unlift(JobStatus.unapply))

  implicit lazy val _jobStatusWrite: Writes[JobStatus] = jobStatusWrite

  def deployResultWrite: Writes[DeployResult] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "success").write[Boolean] and
    (JsPath \ "ports-forwarding").write[Map[String, String]] and
    (JsPath \ "images").write[List[String]] and
    (JsPath \ "status").writeNullable[JobStatus] and
    (JsPath \ "vertex-id").writeNullable[Long]
  )(unlift(DeployResult.unapply))





}
