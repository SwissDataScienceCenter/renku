package backends

import javax.inject.{Inject, Singleton}

import ch.datascience.service.models.deployment.DeploymentRequest
import play.api.Configuration

import scala.concurrent.Future

/**
  * Created by johann on 10/07/17.
  */
@Singleton
class DockerDeployer @Inject()(configuration: Configuration) extends DeployerBackend {

  def create(userId: String, request: DeploymentRequest): Future[String] = request.deploymentType match {
    case "docker" => create2(userId, request)
    case _ => Future.failed( new IllegalArgumentException("Can only deploy containers") )
  }

  def create2(userId: String, request: DeploymentRequest): Future[String] = ???

}
