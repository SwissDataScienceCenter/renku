package models

/**
  * Created by johann on 10/07/17.
  */
trait DeploymentRequest {

  def backend: String

  def parentId: Option[Long]

  def environment: Map[String, String]

}
