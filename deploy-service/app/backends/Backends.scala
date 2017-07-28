package backends

import javax.inject.{ Inject, Singleton }

import play.api.Configuration
import play.api.inject.{ BindingKey, Injector }

@Singleton
class Backends @Inject() ( injector: Injector, configuration: Configuration ) {

  val map: Map[String, DeployerBackend] = loadBackends

  def getBackend( name: String ): Option[DeployerBackend] = map.get( name )

  private[this] def loadBackends: Map[String, DeployerBackend] = {
    val it = for {
      conf <- configuration.getConfig( "deploy.backend" )
    } yield for {
      name <- conf.subKeys
      if conf.getBoolean( s"$name.enabled" ).getOrElse( false )
    } yield {
      val key = BindingKey( classOf[DeployerBackend] ).qualifiedWith( name )
      name -> injector.instanceOf( key )
    }

    it.getOrElse( Seq.empty ).toMap
  }

}
