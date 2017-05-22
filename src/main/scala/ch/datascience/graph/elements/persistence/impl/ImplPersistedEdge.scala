package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.Properties
import ch.datascience.graph.elements.persistence.PersistedEdge

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplPersistedEdge[
  +Id,
  Key,
  +Value
](
  id: Id,
  from: Id,
  to: Id,
  properties: Properties[Key, Value, ImplPersistedRecordProperty[Key, Value]]
) extends PersistedEdge[Id, Key, Value, ImplPersistedRecordProperty[Key, Value], Id]
