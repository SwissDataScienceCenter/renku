package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.mutation.UpdateOperation
import ch.datascience.graph.elements.persistence.{Path, PersistedRecordProperty, PropertyPathFromRecord}

/**
  * Created by jeberle on 10.05.17.
  */
case class UpdatePropertyOperation[+Key, +Value, +T <: PersistedRecordProperty[Key, Value]](
property : T,
new_value : Value

) extends UpdateOperation[PropertyPathFromRecord[Key], Value, T]
