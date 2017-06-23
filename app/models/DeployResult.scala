package models

/**
  * Created by jeberle on 09.06.17.
  */
case class DeployResult(deployId: String, success: Boolean, portForwarding: Map[String, String], images: List[String], message: String)

