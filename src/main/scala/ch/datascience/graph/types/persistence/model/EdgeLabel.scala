package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.types.Multiplicity


/**
  * Created by johann on 09/05/17.
  */
case class EdgeLabel(
  id: UUID,
  graphDomainId: UUID,
  name: String,
  multiplicity: Multiplicity = Multiplicity.Simple
) extends AbstractEntity {

  final override val entityType: EntityType = EntityType.EdgeLabel

  def toRichEdgeLabel(graphDomain: GraphDomain): RichEdgeLabel = RichEdgeLabel(id, graphDomain, name, multiplicity)

}
