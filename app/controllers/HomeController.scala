package controllers


import collection.JavaConversions._
import javax.inject._
import java.util.UUID
import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json.JsResultException

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import java.sql.SQLException

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.ShutdownSignalException
import com.rabbitmq.client.ShutdownListener

import models.EventRepo
import models.Event
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * This controller creates an `Action` to handle HTTP requests to the KG
 *
 * TODO: Add security capability to verify that the HTTP request was granted the appropriate access rights from a trusted authority service
 * TODO: Encapsulate security capability in MQ message so that consumer at receiving end can validate access rights
 * TODO: Handle MQ and DB disconnections appropriately
 *
 */
@Singleton
class HomeController @Inject()(@NamedDatabase("default") dbConfigProvider: DatabaseConfigProvider, eventRepo : EventRepo, configuration : Configuration) extends Controller {

  class KGWALException(val message : String) extends Exception(message)

  val exchangeName = configuration.getString("rabbitmq.exchange").getOrElse("sys")
  val queueName = configuration.getString("rabbitmq.queue").getOrElse("kg")
  val routingKey = configuration.getString("rabbitmq.routing").getOrElse("kgwal")
  val host = configuration.getString("rabbitmq.host").get
  val port = configuration.getInt("rabbitmq.port").getOrElse(5672)
  val user = configuration.getString("rabbitmq.user").get
  val password = configuration.getString("rabbitmq.password").get

  val connection = conn()

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  lazy val db = dbConfig.db

  /**
   * Initialize the rabbitmq client
   */
  def conn() : Connection = {
    println("Create connection")
    val connFactory = new ConnectionFactory()
    connFactory.setUsername(user)
    connFactory.setPassword(password)
    connFactory.setVirtualHost("/")
    connFactory.setHost(host)
    connFactory.setPort(port)
    val conn = connFactory.newConnection()
    val chan = conn.createChannel()
    conn.addShutdownListener(new ShutdownListener() {
         // -- https://www.rabbitmq.com/api-guide.html
         //    TODO: find a reliable way to handle disconnections
         def shutdownCompleted(cause : ShutdownSignalException) : Unit = {
             println("-- Connection closed --")
         }
      })
    chan.exchangeDeclare(exchangeName, "direct", true)
    chan.queueDeclare(queueName, true, false, false, null)
    chan.queueBind(queueName, exchangeName, routingKey)
    chan.close()
    return conn
  }

  /**
   * Create an Action to render an HTML page.
   *
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  /**
   * Create an Action to log an event
   *
   */
  def event = Action.async { implicit request =>
    // We do not use Action.async(parse.json) in order to control the error message ourself
    import dbConfig.profile.api._
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    // Expecting json body (Content-Type: application/json)
    val asyncAction = Future { jsonBody.map { json =>
          val uuid = (json \ "uuid").asOpt[String]
          val evt  = (json \ "event").as[JsValue]
          val event : Event = Await.result(uuid match {
                                case None => eventRepo.insert(evt) 
                                case Some(uuid) => eventRepo.insert(UUID.fromString(uuid), evt)
                              }, Duration.Inf)
          val headers = mapAsJavaMap(Map(("uuid",event.uuid.toString()))).asInstanceOf[java.util.Map[String,Object]]
          val chan = this.connection.createChannel()
          chan.basicPublish(exchangeName, routingKey, new BasicProperties.Builder()
               .headers(headers)
               //.priority(1)
               //.deliveryMode(2)
               //.userId(user)
               //.expiration(84600)
               .contentType("application/json")
               .build(), event.event.getBytes()
          )
          chan.close()
          Created(event.uuid + ", " + event.event + ", " + event.created)
        }.getOrElse {
          throw new KGWALException("Expecting text/json or application/json content type")
        }
    }

    asyncAction recover {
        case e : SQLException => Conflict("SQL " + e.getMessage())
        case e : KGWALException => BadRequest(e.getMessage())
        case e : JsResultException => BadRequest("Invalid JSON object '" + jsonBody.getOrElse("null") + "', expect { uuid='...', event=... }")
        case _ =>  BadRequest("Cannot log event")
    } 
  }
}
