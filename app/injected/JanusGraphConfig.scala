package injected

import javax.inject.{Inject, Named}

import com.typesafe.config.{Config, ConfigFactory}
import groovy.lang.Singleton
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.diskstorage.configuration.ReadConfiguration
import play.api.libs.concurrent.{ActorSystemProvider, Akka}

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 12/04/17.
  */
class JanusGraphConfig @Inject()(protected val actorSystemProvider: ActorSystemProvider) {

  protected lazy val config: Config = ConfigFactory.load().getConfig("janusgraph")

  def get: String = config.getString("file")

  def getExecutionContext: ExecutionContext = actorSystemProvider.get.dispatchers.lookup("janusgraph-execution-context")

}
