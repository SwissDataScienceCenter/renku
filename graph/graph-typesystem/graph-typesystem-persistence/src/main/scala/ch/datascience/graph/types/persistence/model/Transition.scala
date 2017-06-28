package ch.datascience.graph.types.persistence.model

import java.time.Instant
import java.util.UUID

/**
  * Created by johann on 17/03/17.
  */
case class Transition(entityId: UUID, from: Long, toState: EntityState, toTimestamp: Instant) extends Row
