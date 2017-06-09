package controllers

import javax.inject._

import play.api.mvc._
import io.fabric8.kubernetes.api.model.{Namespace, NamespaceBuilder, ServiceAccountBuilder, ServiceSpecBuilder}
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class DeployController @Inject()(config: play.api.Configuration, val playSessionStore: PlaySessionStore) extends Controller {

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  def deploy = Action { implicit request => {
    val profile = getProfiles(request).head
    val kconfig = new ConfigBuilder().build()
    val client = new DefaultKubernetesClient(kconfig)

    def uuid = java.util.UUID.randomUUID.toString()

    val ns = new NamespaceBuilder()
      .withNewMetadata
      .withName(profile.getId)
      .addToLabels("user", profile.getEmail.replace('@','_'))
      .endMetadata
      .build

    val myns = client.namespaces.withName(profile.getId).createOrReplace(ns)

    val deployment = new DeploymentBuilder()
      .withNewMetadata
      .withName("deploy")
      .endMetadata
      .withNewSpec
      .withReplicas(1)
      .withNewTemplate
      .withNewMetadata
      .addToLabels("app", "jupyter")
      .endMetadata
      .withNewSpec
      .addNewContainer
      .withName("jupyter")
      .withImage("jupyter/scipy-notebook")
      .addNewPort
      .withContainerPort(8888)
      .endPort
      .endContainer
      .endSpec
      .endTemplate
      .endSpec
      .build
    val deploy = client.extensions.deployments.inNamespace(profile.getId).create(deployment)

    val nbservice = client.services().inNamespace(profile.getId).createNew()
      .withNewMetadata.withName("nbservice").endMetadata()
      .withNewSpec()
      .withType("LoadBalancer")
      .addToSelector("app", "jupyter")
      .addNewPort().withPort(8888).withNewTargetPort().withIntVal(ewaewawwwww).endTargetPort().withNodePort(30001).endPort()
      .endSpec()
      .done()
    client.close()
    Ok("")
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
