package persistence.scope

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.scope.persistence.remote.StandardClient
import play.api.libs.ws.WSClient

/**
  * Created by johann on 13/06/17.
  */
@Singleton
class ScopeClient @Inject()(
  protected val ws: WSClient,
  protected val scopeClientBaseURLProvider: ScopeClientBaseURLProvider
) extends StandardClient(wsClient = ws, baseUrl = scopeClientBaseURLProvider.get)
