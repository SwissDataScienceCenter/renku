package controllers

import javax.inject._

import ch.datascience.graph.elements.detached.{DetachedProperty, DetachedRichProperty}
import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.create.{CreateVertexOperation, CreateVertexPropertyOperation}
import ch.datascience.graph.elements.new_.{NewRichProperty, NewVertex}
import ch.datascience.graph.elements.persisted.VertexPath
import ch.datascience.graph.elements.{SetValue, SingleValue}
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values._
import clients.GraphClient
import io.fabric8.kubernetes.api.model.{EnvVarBuilder, JobBuilder, NamespaceBuilder}
import io.fabric8.kubernetes.client.{ConfigBuilder, DefaultKubernetesClient}
import models.json._
import models.{DeployRequest, DeployResult}
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

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
                Map(),
                List(),
                "A deployment with this id already exists"))))
      } else {
        val gc = new GraphClient
        val did = NamespaceAndName("deploy:id")
        val dimage = NamespaceAndName("deploy:image")
        val dstatus = NamespaceAndName("deploy:status")
        val dtime = NamespaceAndName("system:creation_time")
        val mut = Mutation(
          Seq(CreateVertexOperation(NewVertex(
            1,
            Set(NamespaceAndName("deploy:deployment")),
            Map(
              did -> SingleValue(
                DetachedRichProperty(did,
                  StringValue(deployRequest.deployId),
                  Map())),
              dimage -> SingleValue(
                DetachedRichProperty(dimage,
                  StringValue(deployRequest.dockerImage),
                  Map())),
              dstatus -> SetValue(List(
                DetachedRichProperty(dstatus,
                  StringValue("submitted"),
                  Map(dtime -> DetachedProperty(dtime, LongValue(System.currentTimeMillis)))
                )
              ))
            )
          ))))

        def getVertexId(result: JsValue): Long = {
          Thread.sleep(1000)
          val status = gc.status((result \ "uuid").as[String])
          val s = Await.result(status, 5.seconds)
          if ((s \ "status").as[String].equals("completed"))
            (s \ "response" \ "event" \ "results" \ 0 \ "id").as[Long]
          else
            getVertexId(result)
        }

        gc.create(mut).map(result => getVertexId(result)).map(id => {

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

          val ns = new NamespaceBuilder().withNewMetadata
            .withName(profile.getId)
            .addToLabels("user", profile.getEmail.replace('@', '_'))
            .endMetadata
            .build

          client.namespaces.withName(profile.getId).createOrReplace(ns)

          val job = new JobBuilder().withNewMetadata().withName(deployRequest.deployId).endMetadata
            .withNewSpec()
            .withActiveDeadlineSeconds(180L)
            .withNewTemplate()
            .withNewMetadata
            .addToLabels("app", deployRequest.deployId)
            .endMetadata
            .withNewSpec
            .withRestartPolicy("Never")
            .addNewContainer
            .withName(deployRequest.deployId)
            .withImage(deployRequest.dockerImage)
            .withNewLifecycle()
            .withNewPostStart()
            .withNewHttpGet()
            .withHost("internal.datascience.ch").withNewPort(9002)
            .withPath("event/" + id + "/poststart/?token=" + request.headers.get("Authorization").getOrElse("Bearer ").substring(7))
            .endHttpGet()
            .endPostStart()
            .withNewPreStop()
            .withNewHttpGet()
            .withHost("internal.datascience.ch").withNewPort(9002)
            .withPath("event/" + id + "/prestop/?token=" + request.headers.get("Authorization").getOrElse("Bearer ").substring(7))
            .endHttpGet()
            .endPreStop()
            .endLifecycle()


          /* val container = new DeploymentBuilder().withNewMetadata
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
             .withImage(deployRequest.dockerImage)*/

          val envs = deployRequest.env.getOrElse(Map())

          val containerEnv = job.withEnv(
            (envs + ("SDSC_GRAPH_ID" -> id.toString, "SDSC_TOKEN" -> request.headers.get("Authorization").getOrElse(""))).map {
              case (k, v) => new EnvVarBuilder().withName(k).withValue(v).build
            }.toList)

          val c = deployRequest.networkPort match {
            case Some(ports) =>
              ports.foldLeft(containerEnv)((ctn, port) => ctn.addNewPort.withContainerPort(port).endPort)
            case _ => containerEnv
          }

          val deployment = c.endContainer.endSpec.endTemplate.endSpec.build()

          /*client.extensions.deployments
            .inNamespace(profile.getId)
            .create(deployment)
*/

          client.extensions().jobs().inNamespace(profile.getId).create(deployment)


          if (deployRequest.networkPort.isDefined) {
            val service = client.services
              .inNamespace(profile.getId)
              .createNew()
              .withNewMetadata
              .withName(deployRequest.deployId)
              .endMetadata
              .withNewSpec
              .withType("LoadBalancer")
              .addToSelector("app", deployRequest.deployId)
            deployRequest.networkPort.get.foldLeft(service)((srv, port) => {
              val external = getAvailablePort
              srv.addNewPort
                .withPort(external)
                .withName("port" + external.toString)
                .withNewTargetPort
                .withIntVal(port)
                .endTargetPort
                .endPort
            }
            ).endSpec.done()
          }

          client.close()

          Ok(
            Json.toJson(
              DeployResult(deployRequest.deployId,
                true,
                Map(),
                List(deployRequest.dockerImage),
                "deploying : " + id)))
        }
        )
      }
  }

  def status(id: String) = Action.async { implicit request =>
    Future {
      val profile = getProfiles(request).head
      val kconfig = new ConfigBuilder().build()
      val client = new DefaultKubernetesClient(kconfig)

      val result: DeployResult =
        if (client.extensions.jobs
          .inNamespace(profile.getId)
          .withName(id)
          .get() == null) {
          DeployResult(id, true, Map(), List(), "un-deployed")
        }
        else {
          val images = client.extensions.jobs
            .inNamespace(profile.getId)
            .withName(id)
            .get.getSpec.getTemplate.getSpec.getContainers
            .map(container => container.getImage)

          if (client.services
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

            val endpoints = ip.map(_ip => client.services
              .inNamespace(profile.getId)
              .withName(id)
              .get
              .getSpec
              .getPorts
              .map(_port => (_port.getTargetPort.getIntVal.toString, _ip + ":" + _port.getPort)))

            endpoints match {
              case Some(l) => DeployResult(id, true, l.toMap, images.toList, "service ready")
              case None => DeployResult(id, true, Map(), images.toList, "no service available")
            }
          } else {
            DeployResult(id, true, Map(), images.toList, "deploying")
          }
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
          client.extensions.jobs
            .inNamespace(profile.getId)
            .list
            .getItems
            .map(deployment =>
              DeployResult(
                deployment.getMetadata.getName,
                true,
                Map(),
                deployment.getSpec.getTemplate.getSpec.getContainers
                  .map(container => container.getImage).toList,
                ""
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
      client.extensions.jobs
        .inNamespace(profile.getId)
        .withName(id)
        .delete()
      client.services.inNamespace(profile.getId).withName(id).delete()
      client.close()
      Ok(Json.toJson(DeployResult(id, true, Map(), List(), "un-deployed")))
    }
  }

  def register(id: String) = Action { implicit request =>
    Ok("")
  }

  def prestop(id: Long) = Action.async { implicit request =>
    val gc = new GraphClient
    val timekey = NamespaceAndName("system:creation_time")
    val reason = request.getQueryString("reason").getOrElse("completed")
    val mut = Mutation(
      Seq(CreateVertexPropertyOperation(NewRichProperty(
        VertexPath(id), NamespaceAndName("deploy:status"), StringValue(reason), Map(timekey -> DetachedProperty(timekey, LongValue(System.currentTimeMillis)))
      )))
    )
    gc.create(mut).map(s => {
      println(s.toString())
      Ok("")
    })
  }

  def poststart(id: Long) = Action.async { implicit request =>
    val gc = new GraphClient
    val timekey = NamespaceAndName("system:creation_time")
    val mut = Mutation(
      Seq(CreateVertexPropertyOperation(NewRichProperty(
        VertexPath(id), NamespaceAndName("deploy:status"), StringValue("started"), Map(timekey -> DetachedProperty(timekey, LongValue(System.currentTimeMillis)))
      )))
    )
    gc.create(mut).map(s => {
      println(s.toString())
      Ok("")
    })
  }

}
