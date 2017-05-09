package ch.datascience.graph.types.persistence.model.relational

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model.EntityState

/**
  * Created by johann on 17/03/17.
  */
case class RowState(id: Option[Long], entityId: UUID, state: EntityState, timestamp: Instant)
