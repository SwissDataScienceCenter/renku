package backends

import javax.inject.{Inject, Singleton}

import models.{ContainerDeploymentRequest, Deployment, DeploymentRequest}
import play.api.Configuration

import scala.concurrent.Future

/**
  * Created by johann on 10/07/17.
  */
@Singleton
class DockerDeployer @Inject()(configuration: Configuration) extends DeployerBackend {

  def create(userId: String, request: DeploymentRequest): Future[String] = request match {
    case req: ContainerDeploymentRequest => create(userId, req)
    case _ => Future.failed( new IllegalArgumentException("Can only deploy containers") )
  }

  def create(userId: String, request: ContainerDeploymentRequest): Future[String] = ???

}
