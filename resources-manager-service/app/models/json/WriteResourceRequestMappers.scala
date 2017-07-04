package models.json

import models.{CreateBucketRequest, WriteResourceRequest}
import play.api.libs.functional.syntax._
import play.api.libs.json._


object WriteResourceRequestMappers {

  def writeResourceRequestReads: Reads[WriteResourceRequest] = (
      (JsPath \ "app_id").readNullable[Long] and
      (JsPath \ "bucket").read[Long] and
      (JsPath \ "target").read[Either[String, Long]]
    )(WriteResourceRequest.apply _)

  private[this] implicit lazy val targetReader: Reads[Either[String, Long]] = (JsPath \ "type").read[String].flatMap {
    case "filename" => (JsPath \ "filename").read[String].map(s => Left(s))
    case "resource" => (JsPath \ "resource_id").read[Long].map(l => Right(l))
    case t => Reads { json => JsError(s"Usupported type $t") }
  }

  def createBucketRequestReads: Reads[CreateBucketRequest] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "backend").read[String]
  )(CreateBucketRequest.apply _)
}
