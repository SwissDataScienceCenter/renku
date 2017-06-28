package ch.datascience.graph.init.client

import play.api.libs.ws.WSClient

/**
  * Created by johann on 22/06/17.
  */
object WSClientFactory {

  lazy val client: WSClient = makeStandaloneClient()

  private[this] def makeStandaloneClient(): WSClient = {
    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    import play.api.libs.ws.ahc.AhcWSClient

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    AhcWSClient()
  }

}
