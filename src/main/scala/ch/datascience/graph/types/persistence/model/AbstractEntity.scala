package ch.datascience.graph.types.persistence.model

import java.util.UUID

/**
  * Created by johann on 09/05/17.
  */
abstract class AbstractEntity extends Row {

  val id: UUID

  val entityType: EntityType

}
