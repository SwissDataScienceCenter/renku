package ch.datascience.graph.types.persistence.model

import java.time.Instant
import java.util.UUID

/**
  * Created by johann on 17/03/17.
  */
case class State(id: Option[Long], entityId: UUID, state: EntityState, timestamp: Instant) extends Row
