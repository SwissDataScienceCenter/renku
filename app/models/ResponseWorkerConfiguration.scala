package models

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.libs.concurrent.ActorSystemProvider

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class ResponseWorkerConfiguration @Inject()(
  protected val configuration: Configuration,
  protected val actorSystemProvider: ActorSystemProvider
) {

  def getExecutionContext: ExecutionContext = actorSystemProvider.get.dispatchers.lookup("response-worker-execution-context")

}
