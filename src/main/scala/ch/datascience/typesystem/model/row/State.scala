package ch.datascience.typesystem.model.row

import java.time.Instant
import java.util.UUID

import ch.datascience.typesystem.model.EntityState

/**
  * Created by johann on 17/03/17.
  */
case class State(id: Option[Long], entityId: UUID, state: EntityState, timestamp: Instant)
