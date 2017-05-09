package ch.datascience.graph.types.persistence.model.relational

import java.util.UUID

import ch.datascience.graph.types.persistence.model.EntityType

/**
  * Created by johann on 17/03/17.
  */
case class RowEntity(id: UUID, entityType: EntityType)
