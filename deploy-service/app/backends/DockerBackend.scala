package backends

import javax.inject.{Inject, Singleton}

import ch.datascience.service.models.deployment.json.ContainerDeploymentOptionsFormat
import ch.datascience.service.models.deployment.{ContainerDeploymentOptions, DeploymentRequest}
import com.spotify.docker.client.exceptions.ImageNotFoundException
import com.spotify.docker.client.messages._
import com.spotify.docker.client.{DefaultDockerClient, DockerClient, ImageRef}
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.util.Try

/**
  * Created by johann on 10/07/17.
  */
@Singleton
class DockerBackend @Inject()(configuration: Configuration, executionContext: ExecutionContext) extends DeployerBackend {

  /**
    * @param userId  user id
    * @param request deployment request
    * @return deployer response
    */
  def create(userId: String, request: DeploymentRequest, additionalEnv: Map[String, String]): Future[String] = {
    request.options match {
      case Some(jsObj) => jsObj.validate[ContainerDeploymentOptions] match {
        case JsSuccess(opt, _) => create(userId, request, opt, additionalEnv)
        case JsError(e) => Future.failed( new IllegalArgumentException(s"Expected container options") )
      }
      case None => Future.failed( new IllegalArgumentException(s"No container options provided") )
    }
  }

  //TODO: remove container if failed to launch
  def create(userId: String, request: DeploymentRequest, options: ContainerDeploymentOptions, additionalEnv: Map[String, String]): Future[String] = {
    if (options.backend.nonEmpty && !options.backend.contains("docker")) {
      return Future.failed( new IllegalArgumentException(s"Other backend requested")  )
    }

    val portBindings: Map[String, Seq[PortBinding]] = (for {
      port <- options.ports
    } yield {
      val b = PortBinding.of("0.0.0.0", generateAvailablePort())
      s"$port" -> List(b)
    }).toMap

    val javaPortBindings = portBindings.mapValues(_.asJava).asJava

    val hostConfig: HostConfig = HostConfig.builder().portBindings(javaPortBindings).build()

    val augmentedEnviron: Map[String, String] = options.environment ++ additionalEnv
    val env: Seq[String] = (for {
      (key, value) <- augmentedEnviron
    } yield s"$key=$value").toSeq

    val containerConfigBuilder = ContainerConfig.builder()
      .hostConfig(hostConfig)
      .image(options.image)
      .env(env.asJava)

    for (ep <- options.entrypoint) {
      containerConfigBuilder.entrypoint(ep)
    }

    for (cmd <- options.command) {
      containerConfigBuilder.cmd(cmd: _*)
    }

    val containerConfig: ContainerConfig = containerConfigBuilder.build()

    val firstCreationAttempt: Future[ContainerCreation] = Future { blocking { dockerClient.createContainer(containerConfig) } }
    val secondCreationAttempt = firstCreationAttempt.recoverWith {
      case _: ImageNotFoundException => Future {
        blocking {
          val imageRef: ImageRef = new ImageRef(options.image)
          val image = if (imageRef.getTag eq null) s"${options.image}:latest" else options.image
          dockerClient.pull(image)
          dockerClient.createContainer(containerConfig)
        }
      }
    }

    val futureId: Future[String] = for {
      creation <- secondCreationAttempt
    } yield {
      creation.id()
    }

    futureId.flatMap { backendId =>
      Future {
        blocking { dockerClient.startContainer(backendId) }
      }.map(_ => backendId)
    }
  }

  /**
    *
    * @param backendId backend id of deployment
    * @return () if successful, failed future otherwise
    */
  def terminate(backendId: String): Future[Unit] = {
    val futureStop = Future{
      blocking { dockerClient.killContainer(backendId) } // TODO: remove magic value of 60seconds
    }

    futureStop.flatMap { _ =>
      Future{
        blocking { dockerClient.removeContainer(backendId) }
      }
    }
  }

  /**
    * @return set of active deployment, by backend id
    */
  def list(): Future[Set[String]] = {
    val containers: Future[Seq[Container]] = Future{
      blocking { dockerClient.listContainers() }
    }.map(_.asScala.toSeq)

    for {
      list <- containers
    } yield (for {
      container <- list
    } yield container.id()).toSet
  }

  private[this] lazy val dockerConf: Configuration = configuration.getConfig("deploy.backend.docker").get
  //"unix:///var/run/docker.sock"

  //TODO: handle certificates
  private[this] lazy val dockerClient: DockerClient = {
    DefaultDockerClient.builder()
      .uri(dockerConf.getString("uri").get)
      .build()
  }

  private[this] lazy val random: scala.util.Random = new scala.util.Random

  private[this] def generateAvailablePort(): Int = {
    40000 + random.nextInt(5000)
  }

  private[this] implicit val ec: ExecutionContext = executionContext

}
