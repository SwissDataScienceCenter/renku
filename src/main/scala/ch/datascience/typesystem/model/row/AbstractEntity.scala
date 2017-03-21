package ch.datascience.typesystem.model.row

import java.util.UUID

import ch.datascience.typesystem.model.EntityType

/**
  * Created by johann on 17/03/17.
  */
trait AbstractEntity {

  val id: UUID

  val entityType: EntityType

}
