package models

/**
  * Created by johann on 10/07/17.
  */
case class DockerDeployment(
  id: Long,
  backendId: String,
  parentId: Option[Long],
  image: String,
  name: String,
  environment: Map[String, String] = Map.empty,
  ports: Map[String, String] = Map.empty,
  started: Boolean = false,
  completed: Boolean = false
) extends ContainerDeployment {

  val backend: String = "docker"

}
