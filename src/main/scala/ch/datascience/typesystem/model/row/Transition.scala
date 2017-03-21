package ch.datascience.typesystem.model.row

import java.time.Instant
import java.util.UUID

import ch.datascience.typesystem.model.EntityState

/**
  * Created by johann on 17/03/17.
  */
case class Transition(entityId: UUID, from: Long, toState: EntityState, toTimestamp: Instant)
