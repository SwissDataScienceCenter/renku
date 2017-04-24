package ch.datascience.typesystem.relationaldb.row

import java.util.UUID

import ch.datascience.typesystem.model.EntityType

/**
  * Created by johann on 17/03/17.
  */
case class Entity(id: UUID, entityType: EntityType)
