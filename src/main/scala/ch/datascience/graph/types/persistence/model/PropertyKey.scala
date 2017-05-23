package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.types.{Cardinality, DataType}

/**
  * Created by johann on 16/03/17.
  */
case class PropertyKey(
  id: UUID,
  graphDomainId: UUID,
  name: String,
  dataType: DataType,
  cardinality: Cardinality
) extends AbstractEntity {

  final override val entityType: EntityType = EntityType.PropertyKey

  def toRichPropertyKey(graphDomain: GraphDomain): RichPropertyKey = RichPropertyKey(id, graphDomain, name, dataType, cardinality)

}
