package ch.datascience.graph.elements.mutation.log.model

import java.time.Instant
import java.util.UUID

import play.api.libs.json.JsValue

/**
  * Created by johann on 07/06/17.
  */
case class Event(uuid : UUID, event : JsValue, created : Instant)
