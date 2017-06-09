package controllers

import javax.inject._

import play.api.mvc._
import io.fabric8.kubernetes.api.model.{Namespace, NamespaceBuilder, ServiceAccountBuilder, ServiceSpecBuilder}
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import models.DeployRequest
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import models.json._
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class DeployController @Inject()(config: play.api.Configuration, val playSessionStore: PlaySessionStore) extends Controller with JsonComponent {

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  def deploy = Action.async(bodyParseJson[DeployRequest](deployRequestReads)) { implicit request => {
    val profile = getProfiles(request).head
    val deployRequest: DeployRequest = request.body
    val kubeconfig = new ConfigBuilder().build()
    val client = new DefaultKubernetesClient(kubeconfig)

    val ns = new NamespaceBuilder()
      .withNewMetadata
      .withName(profile.getId)
      .addToLabels("user", profile.getEmail.replace('@','_'))
      .endMetadata
      .build

    client.namespaces.withName(profile.getId).createOrReplace(ns)

    val container = new DeploymentBuilder()
      .withNewMetadata
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

    val c = deployRequest.networkPort match {
      case Some(port) => container.addNewPort.withContainerPort(port).endPort
      case _ => container
    }

    val deployment = c.endContainer.endSpec.endTemplate.endSpec.build()

    client.extensions.deployments.inNamespace(profile.getId).create(deployment)

    if (deployRequest.networkPort.isDefined) {
      client.services.inNamespace(profile.getId).createNew()
        .withNewMetadata.withName(deployRequest.deployId).endMetadata
        .withNewSpec
        .withType("LoadBalancer")
        .addToSelector("app", deployRequest.deployId)
        .addNewPort.withPort(deployRequest.networkPort.get).withNewTargetPort.withIntVal(8888).endTargetPort.endPort
        .endSpec
        .done()
      //client.services.inNamespace(profile.getId).withName(deployRequest.deployId).get.getStatus.getLoadBalancer.getIngress.get(0).getIp
    }

    client.close()
    Future(Ok(""))
  }
  }

  def undeploy = Action { implicit request => {
    val profile = getProfiles(request).head
    val kconfig = new ConfigBuilder().build()
    val client = new DefaultKubernetesClient(kconfig)
    client.extensions.deployments.inNamespace(profile.getId).delete()
    client.extensions.replicaSets.inNamespace(profile.getId).delete()
    client.services.inNamespace(profile.getId).delete()
    client.close()
    Ok("")
  }
  }

  def register(id: Long) = Action { implicit request =>
      Ok("")
  }

}
