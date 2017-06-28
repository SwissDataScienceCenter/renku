package ch.datascience.graph.elements.detached.impl

import ch.datascience.graph.elements.detached.DetachedEdge

/**
  * Created by johann on 29/05/17.
  */
private[detached] case class ImplDetachedEdge(
  label: DetachedEdge#Label,
  from: DetachedEdge#VertexReference,
  to: DetachedEdge#VertexReference,
  properties: DetachedEdge#Properties
) extends DetachedEdge
