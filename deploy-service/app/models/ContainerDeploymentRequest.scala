package models

/**
  * Created by johann on 10/07/17.
  */
case class ContainerDeploymentRequest(
  backend: String,
  parentId: Option[Long] = None,
  image: String,
  environment: Map[String, String] = Map.empty,
  ports: Map[String, String] = Map.empty
) extends DeploymentRequest
