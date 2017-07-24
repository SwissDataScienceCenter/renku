package backends

import ch.datascience.service.models.deployment.{DeploymentRequest, DeploymentResponse}

import scala.concurrent.Future

/**
  * Created by johann on 10/07/17.
  */
trait DeployerBackend {

  /**
    * @param userId user id
    * @param request deployment request
    * @return deployer backend id
    */
  def create(userId: String, request: DeploymentRequest): Future[String]

  /**
    *
    * @param backendId backend id of deployment
    * @return () if successful, failed future otherwise
    */
  def terminate(backendId: String): Future[Unit]

  /**
    * @return set of active deployment, by backend id
    */
  def list(): Future[Set[String]]

}
