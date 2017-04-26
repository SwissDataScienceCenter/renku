package ch.datascience.typesystem.model.relational

import java.util.UUID

import ch.datascience.typesystem.model.EntityType

/**
  * Created by johann on 17/03/17.
  */
abstract class AbstractEntity {

  val id: UUID

  val entityType: EntityType

}
