package controllers

import collection.JavaConversions._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue

import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
//import slick.jdbc.PostgresProfile
//import slick.jdbc.PostgresProfile.api._
//import slick.driver.PostgresDriver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.ShutdownSignalException
import com.rabbitmq.client.ShutdownListener

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(@NamedDatabase("default") dbConfigProvider: DatabaseConfigProvider) extends Controller {

  val exchangeName = "sys"

  val queueName = "kg"

  val routingKey = "kgwal"

  val host = "iccluster108.iccluster.epfl.ch"

  val port = 5672

  val user = "guest"

  val password = "guest"

  val connection = conn()

  val dbConfig = dbConfigProvider.get[JdbcProfile]

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
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def event = Action { implicit request =>
    import dbConfig.profile.api._
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    // Expecting json body (Content-Type: application/json)
    jsonBody.map { json =>
      val oid = (json \ "oid").as[String]
      val evt = (json \ "evt").as[JsValue]
      val chan = this.connection.createChannel()
      println("Received event: " + oid + ", " + evt)
      val headers = mapAsJavaMap(Map(("id",oid))).asInstanceOf[java.util.Map[String,Object]]
      chan.basicPublish(exchangeName, routingKey, new BasicProperties.Builder()
               .headers(headers)
               //.priority(1)
               //.deliveryMode(2)
               //.userId(user)
               //.expiration(84600)
               .contentType("application/json")
               .build(), evt.toString().getBytes()
         )
      chan.close()
      Ok("Got: " + oid + ", " + evt)
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }
}
