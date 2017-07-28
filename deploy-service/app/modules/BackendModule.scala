package modules

import backends.{ DeployerBackend, DockerBackend }
import play.api.inject.{ Binding, Module }
import play.api.{ Configuration, Environment }

/**
 * Created by johann on 07/07/17.
 */
class BackendModule extends Module {

  def bindings( environment: Environment, configuration: Configuration ): Seq[Binding[DeployerBackend]] = {
    for {
      ( name, clazz ) <- availableBindings.toSeq
      if configuration.getBoolean( s"deploy.backend.$name.enabled" ).getOrElse( false )
    } yield bind( classOf[DeployerBackend] ).qualifiedWith( name ).to( clazz )
  }

  protected def availableBindings: Map[String, Class[_ <: DeployerBackend]] = Map(
    "docker" -> classOf[DockerBackend]
  )

}
