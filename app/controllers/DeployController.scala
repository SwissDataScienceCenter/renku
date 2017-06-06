package controllers

import javax.inject._
import play.api.mvc._
import io.fabric8.kubernetes.api.model.NamespaceBuilder
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class DeployController @Inject()(config: play.api.Configuration) extends Controller {

  def deploy = Action.async { implicit request =>
    Future {
      val kconfig = new ConfigBuilder().build()
      val client = new DefaultKubernetesClient(kconfig)
      try { // Create a namespace for all our stuff
        val ns = new NamespaceBuilder().withNewMetadata.withName("thisisatest").addToLabels("this", "rocks").endMetadata.build
        val fabric8 = new ServiceAccountBuilder().withNewMetadata.withName("fabric8").endMetadata.build
        client.serviceAccounts.inNamespace("thisisatest").createOrReplace(fabric8)
        var i = 0
        while ( {
          i < 2
        }) {
          System.err.println("Iteration:" + (i + 1))
          var deployment = new DeploymentBuilder().withNewMetadata.withName("nginx").endMetadata.withNewSpec.withReplicas(1).withNewTemplate.withNewMetadata.addToLabels("app", "nginx").endMetadata.withNewSpec.addNewContainer.withName("nginx").withImage("nginx").addNewPort.withContainerPort(80).endPort.endContainer.endSpec.endTemplate.endSpec.build
          deployment = client.extensions.deployments.inNamespace("thisisatest").create(deployment)
          System.err.println("Scaling up:" + deployment.getMetadata.getName)
          client.extensions.deployments.inNamespace("thisisatest").withName("nginx").scale(2, true)
          System.err.println("Deleting:" + deployment.getMetadata.getName)
          client.resource(deployment).delete

          {
            i += 1; i - 1
          }
        }
      } finally {
        client.namespaces.withName("thisisatest").delete
        client.close()
      }
      Ok()
    }
  }

  def register(id: Long) = Action.async { implicit request =>
    Future {
      Ok()
    }
  }

}
