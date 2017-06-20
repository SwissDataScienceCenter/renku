package controllers

import javax.inject._

import ch.datascience.graph.elements.SingleValue
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.create.CreateVertexOperation
import ch.datascience.graph.elements.new_.NewVertex
import ch.datascience.graph.naming.NamespaceAndName
import clients.GraphClient
import play.api.mvc._
import io.fabric8.kubernetes.api.model.{EnvVarBuilder, NamespaceBuilder}
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import models.{DeployRequest, DeployResult}
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import models.json._
import play.api.libs.json.Json
import ch.datascience.graph.values._
import play.api.libs.ws.WSClient

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class DeployController @Inject()(config: play.api.Configuration,
                                 val playSessionStore: PlaySessionStore,
                                 wsclient: WSClient)
    extends Controller
    with JsonComponent {

  implicit lazy val ec: ExecutionContext =
    play.api.libs.concurrent.Execution.defaultContext
  implicit val ws: WSClient = wsclient
  implicit lazy val host: String = config
    .getString("graph.mutation.service.host")
    .getOrElse("http://localhost:9000/api/mutation/")

  private def getProfiles(
      implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  def deploy = Action.async(bodyParseJson[DeployRequest](deployRequestReads)) {
    implicit request =>
      val profile = getProfiles(request).head
      val deployRequest: DeployRequest = request.body
      val kubeconfig = new ConfigBuilder().build()
      val client = new DefaultKubernetesClient(kubeconfig)

      if (client.extensions.deployments
            .inNamespace(profile.getId)
            .withName(deployRequest.deployId)
            .get() != null) {
        Future(
          Conflict(
            Json.toJson(
              DeployResult(deployRequest.deployId,
                           false,
                           None,
                           "A deployment with this id already exists"))))
      } else {
        val gc = new GraphClient
        val did = NamespaceAndName("sdsc:deploy-id")
        val dimage = NamespaceAndName("sdsc:deploy-image")
        val dtime = NamespaceAndName("sdsc:deploy-time")
        val mut = Mutation(
          Seq(CreateVertexOperation(NewVertex(
            1,
            Set.empty,
            Map(
              did -> SingleValue(
                DetachedRichProperty(did,
                                     StringValue(deployRequest.deployId),
                                     Map())),
              dimage -> SingleValue(
                DetachedRichProperty(dimage,
                                     StringValue(deployRequest.dockerImage),
                                     Map())),
              dtime -> SingleValue(
                DetachedRichProperty(dtime,
                                     LongValue(System.currentTimeMillis),
                                     Map()))
            )
          ))))
        val created = gc.create(mut)

        for {
          result <- created
          _      <- Future(Thread.sleep(1000))
          status <- gc.status((result \ "uuid").as[String])
        } yield {
          val id =
            (status \ "response" \ "event" \ "results" \ 0 \ "id").as[Long]

          val ports = client
            .services()
            .list()
            .getItems
            .flatMap(service =>
              service.getSpec.getPorts.map(port => port.getPort))

          val r = new scala.util.Random

          def getAvailablePort: Int = {
            val p = 40000 + r.nextInt(5000)
            if (ports.contains(p)) getAvailablePort else p
          }

          val external = getAvailablePort

          val ns = new NamespaceBuilder().withNewMetadata
            .withName(profile.getId)
            .addToLabels("user", profile.getEmail.replace('@', '_'))
            .endMetadata
            .build

          client.namespaces.withName(profile.getId).createOrReplace(ns)

          val container = new DeploymentBuilder().withNewMetadata
            .withName(deployRequest.deployId)
            .endMetadata
            .withNewSpec
            .withReplicas(1)
            .withNewTemplate
            .withNewMetadata
            .addToLabels("app", deployRequest.deployId)
            .endMetadata
            .withNewSpec
            .addNewContainer
            .withName(deployRequest.deployId)
            .withImage(deployRequest.dockerImage)
            .withEnv(new EnvVarBuilder()
              .withName("token")
              .withValue(request.headers.get("Authorization").getOrElse(""))
              .build)

          val envs = deployRequest.env.getOrElse(Map()).put("graph_id", id.toString)
          val containerEnv = envs.foldLeft(container){ case (ctn, (k, v)) => ctn.withEnv(new EnvVarBuilder()
              .withName(k)
              .withValue(v)
              .build)
          }

          val c = deployRequest.networkPort match {
            case Some(port) =>
              containerEnv.addNewPort.withContainerPort(port).endPort
            case _ => containerEnv
          }

          val deployment = c.endContainer.endSpec.endTemplate.endSpec.build()

          client.extensions.deployments
            .inNamespace(profile.getId)
            .create(deployment)

          if (deployRequest.networkPort.isDefined) {
            client.services
              .inNamespace(profile.getId)
              .createNew()
              .withNewMetadata
              .withName(deployRequest.deployId)
              .endMetadata
              .withNewSpec
              .withType("LoadBalancer")
              .addToSelector("app", deployRequest.deployId)
              .addNewPort
              .withPort(external)
              .withNewTargetPort
              .withIntVal(deployRequest.networkPort.get)
              .endTargetPort
              .endPort
              .endSpec
              .done()
          }

          client.close()

          Ok(
            Json.toJson(
              DeployResult(deployRequest.deployId,
                           true,
                           None,
                           "deploying : " + id)))
        }
      }
  }

  def status(id: String) = Action.async { implicit request =>
    Future {
      val profile = getProfiles(request).head
      val kconfig = new ConfigBuilder().build()
      val client = new DefaultKubernetesClient(kconfig)

      val result: DeployResult =
        if (client.extensions.deployments
              .inNamespace(profile.getId)
              .withName(id)
              .get() == null) {
          DeployResult(id, true, None, "un-deployed")
        } else if (client.services
                     .inNamespace(profile.getId)
                     .withName(id)
                     .get() != null) {
          val ip = client.services
            .inNamespace(profile.getId)
            .withName(id)
            .get
            .getStatus
            .getLoadBalancer
            .getIngress
            .map(ingress => ingress.getIp)
            .lastOption
          val port = client.services
            .inNamespace(profile.getId)
            .withName(id)
            .get
            .getSpec
            .getPorts
            .map(_port => _port.getPort)
            .lastOption
          ip.flatMap(
              _ip =>
                port.map(
                  _port =>
                    DeployResult(id,
                                 true,
                                 Some("http://" + _ip + ":" + _port),
                                 "ready")))
            .getOrElse(DeployResult(id, true, None, "waiting for service"))
        } else {
          DeployResult(id, true, None, "deploying")
        }

      client.close()
      Ok(Json.toJson(result))
    }
  }

  def list = Action.async { implicit request =>
    Future {
      val profile = getProfiles(request).head
      val kconfig = new ConfigBuilder().build()
      val client = new DefaultKubernetesClient(kconfig)
      Ok(
        Json.toJson(
          client.extensions.deployments
            .inNamespace(profile.getId)
            .list
            .getItems
            .map(deployment =>
              DeployResult(
                deployment.getMetadata.getName,
                true,
                None,
                "with image(s): " +
                  deployment.getSpec.getTemplate.getSpec.getContainers
                    .map(container => container.getImage)
                    .mkString(", ")
            ))
        ))
    }
  }

  def undeploy(id: String) = Action.async { implicit request =>
    Future {
      val profile = getProfiles(request).head
      val kconfig = new ConfigBuilder().build()
      val client = new DefaultKubernetesClient(kconfig)
      client.extensions.deployments
        .inNamespace(profile.getId)
        .withName(id)
        .delete()
      client.extensions.replicaSets
        .inNamespace(profile.getId)
        .withName(id)
        .delete()
      client.services.inNamespace(profile.getId).withName(id).delete()
      client.close()
      Ok(Json.toJson(DeployResult(id, true, None, "un-deployed")))
    }
  }

  def register(id: String) = Action { implicit request =>
    Ok("")
  }

}
