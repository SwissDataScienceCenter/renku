package models

/**
  * Created by johann on 10/07/17.
  */
trait ContainerDeployment extends Deployment {

  def ports: Map[String, String]

  def image: String

}
