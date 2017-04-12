package injected

import com.typesafe.config.ConfigFactory

/**
  * Created by johann on 12/04/17.
  */
class JanusGraphConfig {

  def get: String = {
    val config = ConfigFactory.load().getConfig("janusgraph")
    val using = config.getString("default")
    config.getString(s"configs.$using.file")
  }

}
