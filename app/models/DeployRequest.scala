package models
/**
  * Created by jeberle on 09.06.17.
  */
case class DeployRequest(deployId: String, dockerImage: String, networkPort: Option[Int])
