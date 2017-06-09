package models

import play.api.libs.json._

/**
  * Created by johann on 25/04/17.
  */
package object json {

  implicit lazy val deployRequestReads: Reads[DeployRequest] = DeployRequestMappers.deployRequestReads
  implicit lazy val deployResultWrite: Writes[DeployResult] = DeployResultMappers.deployResultWrite

}
