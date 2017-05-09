package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.types.persistence.model.relational.RowAbstractEntity

/**
  * Created by johann on 17/03/17.
  */
abstract class AbstractEntity[+Row <: RowAbstractEntity] extends MapsToRow[Row] {

  val id: UUID

  val entityType: EntityType

}
