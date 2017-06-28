package models

import java.util.UUID
import javax.inject.Singleton

import ch.datascience.graph.elements.mutation.worker.Queue
import play.api.libs.json.JsValue

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class WorkerQueue extends Queue[(UUID, JsValue)]
