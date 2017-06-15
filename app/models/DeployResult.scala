package models

/**
  * Created by jeberle on 09.06.17.
  */
case class DeployResult(deployId: String, success: Boolean, externalUrl: Option[String], message: String)

