package ch.datascience.graph.types.persistence.model

import java.util.UUID

/**
  * Created by johann on 17/03/17.
  */
case class Entity(id: UUID, entityType: EntityType) extends Row
