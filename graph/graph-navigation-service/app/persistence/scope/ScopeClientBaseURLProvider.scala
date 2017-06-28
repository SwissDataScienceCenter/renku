package persistence.scope

import javax.inject.{Inject, Singleton}

import play.api.Configuration

/**
  * Created by johann on 13/06/17.
  */
@Singleton
class ScopeClientBaseURLProvider @Inject()(protected val configuration: Configuration) {

  def get: String = configuration.getString("graph.scope.remote.url").get

}
