package ch.datascience.graph.elements.new_.impl

import ch.datascience.graph.elements.new_.NewVertex

/**
  * Created by jeberle on 15.05.17.
  */
private[new_] case class ImplNewVertex(
  tempId: NewVertex#TempId,
  types: Set[NewVertex#TypeId],
  properties: NewVertex#Properties
) extends NewVertex
