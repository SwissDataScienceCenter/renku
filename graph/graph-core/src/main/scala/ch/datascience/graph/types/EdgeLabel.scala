package ch.datascience.graph.types

import ch.datascience.graph.Constants
import ch.datascience.graph.bases.HasKey
import ch.datascience.graph.types.impl.ImplEdgeLabel

trait EdgeLabel extends HasKey {

  final type Key = Constants.Key

  def multiplicity: Multiplicity

}

object EdgeLabel {

  def apply(key: EdgeLabel#Key, multiplicity: Multiplicity): EdgeLabel = ImplEdgeLabel(key, multiplicity)

  def unapply(edgeLabel: EdgeLabel): Option[(edgeLabel.Key, Multiplicity)] = {
    if (edgeLabel eq null)
      None
    else
      Some(edgeLabel.key, edgeLabel.multiplicity)
  }

}
