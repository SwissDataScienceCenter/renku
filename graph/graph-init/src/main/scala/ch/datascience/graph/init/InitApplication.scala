package ch.datascience.graph.init

import java.io.{FileNotFoundException, InputStream}

import akka.actor.ActorSystem
import ch.datascience.graph.init.client._
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.persistence.model
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.Try

/**
  * Created by johann on 21/06/17.
  */
object InitApplication {

  def main(args: Array[String]): Unit = {
    app()
  }

  def app(): Unit = {
    val config: Config = ConfigFactory.load()

    val script: String = config.getString("application.script")
    val typeInitJson: JsValue = Json.parse(readResource(s"/$script"))
    val typeInit = typeInitJson.validate[TypeInit](TypeInitFormat) match {
      case JsSuccess(ti, _) => ti
      case e: JsError => throw new RuntimeException(Json.prettyPrint(JsError.toJson(e)))
    }

    val client: WSClient = WSClientFactory.client
    val endPromise: Promise[Unit] = Promise()
    endPromise.future.onComplete{ _ => client.close(); ActorSystem().terminate(); sys.exit() }

    var currentFuture: Future[Any] = null

    currentFuture = waitForApi(client, config)
    currentFuture.onComplete(println)

    currentFuture = currentFuture.flatMap{ _ => initSystemPropertyKeys(client, config, typeInit) }
    currentFuture.onComplete(println)

    currentFuture = currentFuture.flatMap(_ => initGraphDomain(client, config, typeInit))
    currentFuture.onComplete(println)

    currentFuture = currentFuture.flatMap(_ => initPropertyKeys(client, config, typeInit))
    currentFuture.onComplete(println)

    currentFuture = currentFuture.flatMap(_ => initEdgeLabels(client, config, typeInit))
    currentFuture.onComplete(println)

    currentFuture = currentFuture.flatMap(_ => initNamedTypes(client, config, typeInit))
    currentFuture.onComplete(println)

    currentFuture.onComplete{ _ => endPromise.success(()) }

  }

  def readResource(resource: String): InputStream = {
    Try(getClass.getResourceAsStream(resource))
      .recover{ case _ => throw new FileNotFoundException(resource)}
      .get
  }

  def waitForApi(client: WSClient, config: Config): Future[Unit] = {
    val url = s"${config.getString("graph.api.types")}/management/graph_domain"

    def check(): Future[Unit] = {
      println(s"Checking: $url")
      val f = client.url(url).withRequestTimeout(30.seconds).get()
      f.onComplete(println)
      for {
        response <- f
      } yield response.status match {
        case 200 => ()
        case _ => println(response); throw new RuntimeException(response.statusText)
      }
    }

    def checkN(n: Int): Future[Unit] = {
      require(n > 0)
      println(s"$n tries remaining")
      n match {
        case 1 => check()
        case _ => check().recoverWith{ case _ =>
          scala.concurrent.blocking { Thread.sleep(10000) }
          checkN(n - 1)
        }
      }
    }

    checkN(10)
  }

  def initSystemPropertyKeys(client: WSClient, config: Config, typeInit: TypeInit): Future[Seq[model.SystemPropertyKey]] = {
    val spkc = new SystemPropertyKeyClient(config.getString("graph.api.types"), client)
    Future.traverse(typeInit.systemPropertyKeys){ pk => spkc.getOrCreateSystemPropertyKey(pk.name, pk.dataType, pk.cardinality) }
  }

  def initGraphDomain(client: WSClient, config: Config, typeInit: TypeInit): Future[Seq[model.GraphDomain]] = {
    val gdc = new GraphDomainClient(config.getString("graph.api.types"), client)
    Future.traverse(typeInit.graphDomains){ gd => gdc.getOrCreateGraphDomain(gd) }
  }

  def initPropertyKeys(client: WSClient, config: Config, typeInit: TypeInit): Future[Seq[model.RichPropertyKey]] = {
    val pkc = new PropertyKeyClient(config.getString("graph.api.types"), client)
    Future.traverse(typeInit.propertyKeys){ pk =>
      val NamespaceAndName(namespace, name) = pk.key
      pkc.getOrCreatePropertyKey(namespace, name, pk.dataType, pk.cardinality)
    }
  }

  def initEdgeLabels(client: WSClient, config: Config, typeInit: TypeInit): Future[Seq[model.RichEdgeLabel]] = {
    val elc = new EdgeLabelClient(config.getString("graph.api.types"), client)
    Future.traverse(typeInit.edgeLabels){ el =>
      val NamespaceAndName(namespace, name) = el.key
      elc.getOrCreateEdgeLabel(namespace, name, el.multiplicity)
    }
  }

  def initNamedTypes(client: WSClient, config: Config, typeInit: TypeInit): Future[Seq[model.RichNamedType]] = {
    val ntc = new NamedTypeClient(config.getString("graph.api.types"), client)
    val promiseMap = (for {
      namedType <- typeInit.namedTypes
    } yield namedType.typeId -> Promise[model.RichNamedType]()).toMap

    Future.traverse(typeInit.namedTypes){ nt =>
      val NamespaceAndName(namespace, name) = nt.typeId
      val superTypesDone = Future.traverse(nt.superTypes){ st => promiseMap(st).future }
      val res = superTypesDone.flatMap{ _ => ntc.getOrCreateNamedType(namespace, name, nt.superTypes.toSeq, nt.properties.toSeq) }
      res.onComplete{ result => promiseMap(nt.typeId).complete(result) }
      res
    }
  }

}
