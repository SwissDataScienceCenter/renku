package controllers

import collection.JavaConversions._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel
import com.rabbitmq.client.AMQP.BasicProperties

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  val exchangeName = "sys"

  val queueName = "kg"

  val routingKey = "kgwal"

  val connection = conn()

  /**
   * Initialize the rabbitmq client
   */
  def conn() : Connection = {
    println("Create connection")
    val connFactory = new ConnectionFactory()
    connFactory.setUsername("guest")
    connFactory.setPassword("guest")
    connFactory.setVirtualHost("/")
    connFactory.setHost("iccluster108.iccluster.epfl.ch")
    connFactory.setPort(5672)
    val conn = connFactory.newConnection()
    val chan = conn.createChannel()
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
