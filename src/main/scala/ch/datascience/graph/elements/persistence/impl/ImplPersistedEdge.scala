package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.{BoxedValue, MultiProperties}
import ch.datascience.graph.elements.persistence.PersistedEdge

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplPersistedEdge[
  +Id,
  Key,
  +Value,
  +PropId
](
  id: Id,
  from: Id,
  to: Id,
  properties: MultiProperties[Key, Value, ImplPersistedMultiRecordProperty[PropId, Key, Value]]
) extends PersistedEdge[Id, Key, Value, ImplPersistedMultiRecordProperty[PropId, Key, Value], Id]
