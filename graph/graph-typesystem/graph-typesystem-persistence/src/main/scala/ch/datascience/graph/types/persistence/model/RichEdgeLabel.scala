package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.Multiplicity

/**
  * Created by johann on 07/06/17.
  */
class RichEdgeLabel(
  id: UUID,
  val graphDomain: GraphDomain,
  name: String,
  multiplicity: Multiplicity
) extends EdgeLabel(
  id,
  graphDomain.id,
  name,
  multiplicity
) with RichAbstractEntity[EdgeLabel] {

  def key: NamespaceAndName = NamespaceAndName(graphDomain.namespace, name)

}

object RichEdgeLabel {

  def apply(id: UUID, graphDomain: GraphDomain, name: String, multiplicity: Multiplicity): RichEdgeLabel = {
    new RichEdgeLabel(id, graphDomain, name, multiplicity)
  }

  def unapply(edgeLabel: RichEdgeLabel): Option[(UUID, GraphDomain, String, Multiplicity)] = {
    if (edgeLabel eq null)
      None
    else
      Some((edgeLabel.id, edgeLabel.graphDomain, edgeLabel.name, edgeLabel.multiplicity))
  }

}
