package backends

import ch.datascience.service.models.deployment.DeploymentRequest

import scala.concurrent.Future

/**
  * Created by johann on 10/07/17.
  */
trait DeployerBackend {

  /**
    *
    * @param userId
    * @param request
    * @return deployer backend id
    */
  def create(userId: String, request: DeploymentRequest): Future[String]

}
