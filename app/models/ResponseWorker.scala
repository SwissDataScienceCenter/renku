package models

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.mutation.worker.{ResponseWorker => Base}
import play.api.Configuration

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class ResponseWorker @Inject()(
  override protected val queue: WorkerQueue,
  protected val graphProvider: GraphProvider,
  override protected val dao: ResponseDAO,
  protected val config: ResponseWorkerConfiguration
) extends Base(
  queue = queue,
  graph = graphProvider.graph,
  dao = dao,
  ec = config.getExecutionContext
)
