package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.BoxedOrValidValue
import ch.datascience.graph.elements.persistence.{NewRecordProperty, Path}

/**
  * Created by johann on 11/05/17.
  */
case class ImplNewRecordProperty[+Key, +Value: BoxedOrValidValue](
  parent: Path,
  key: Key,
  value: Value
) extends NewRecordProperty[Key, Value, ImplNewRecordProperty[Key, Value]]
