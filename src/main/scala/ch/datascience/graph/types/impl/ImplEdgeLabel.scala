package ch.datascience.graph.types.impl

import ch.datascience.graph.types._

/**
  * Created by johann on 12/05/17.
  */
private[types] final case class ImplEdgeLabel(
  key: PropertyKey#Key,
  multiplicity: Multiplicity
) extends EdgeLabel {

  override def toString: String = s"EdgeLabel($key, $multiplicity)"

}
