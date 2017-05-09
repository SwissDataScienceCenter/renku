package ch.datascience.graph.types.persistence.model.relational

import java.util.UUID

import ch.datascience.graph.types.persistence.model.EntityType

/**
  * Created by johann on 09/05/17.
  */
abstract class RowAbstractEntity extends RowType {

  val id: UUID

  val entityType: EntityType

}
