package ch.datascience.graph.elements.detached.impl

import ch.datascience.graph.elements.detached.DetachedVertex

/**
  * Created by johann on 29/05/17.
  */
private[detached] case class ImplDetachedVertex(
  types: Set[DetachedVertex#TypeId],
  properties: DetachedVertex#Properties
) extends DetachedVertex
