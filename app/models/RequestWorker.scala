package models

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.mutation.worker.{RequestWorker => Base}

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class RequestWorker @Inject()(
  override protected val queue: WorkerQueue,
  override protected val dao: RequestDAO
) extends Base(
  queue = queue,
  dao = dao,
  ec = play.api.libs.concurrent.Execution.defaultContext
)
