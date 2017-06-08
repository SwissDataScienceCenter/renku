package models

import javax.inject.Inject

import play.api.libs.concurrent.ActorSystemProvider
import play.api.{Configuration, Environment}

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 12/04/17.
  */
class JanusGraphConfig @Inject()(protected val actorSystemProvider: ActorSystemProvider, protected val env: Environment, protected val configuration: Configuration) {

  protected lazy val config: Configuration = configuration.getConfig("janusgraph").get

  def get: String = env.getFile(config.getString("file").get).getPath

}
