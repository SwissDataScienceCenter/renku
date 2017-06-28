package ch.datascience.graph.types.persistence.model

import java.util.UUID

/**
  * Created by johann on 09/05/17.
  */
case class GraphDomain(id: UUID, namespace: String) extends AbstractEntity with RichAbstractEntity[GraphDomain] {

  final override val entityType: EntityType = EntityType.GraphDomain

}
