package ch.datascience.typesystem.model.relational

import java.util.UUID

import ch.datascience.typesystem.model.EntityType

/**
  * Created by johann on 15/03/17.
  */
case class GraphDomain(id: UUID, namespace: String) extends AbstractEntity {

  final override val entityType: EntityType = EntityType.GraphDomain

}
