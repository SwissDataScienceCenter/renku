package persistence.graph

import javax.inject.Inject

import play.api.{Configuration, Environment}

/**
  * Created by johann on 12/04/17.
  */
class JanusGraphConfigProvider @Inject()(protected val env: Environment, protected val configuration: Configuration) {

  protected lazy val config: Configuration = configuration.getConfig("janusgraph").get

  def get: String = env.getFile(config.getString("file").get).getPath

}
