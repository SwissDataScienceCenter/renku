package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.BoxedOrValidValue
import ch.datascience.graph.elements.persistence.{NewMultiRecordProperty, Path}

/**
  * Created by johann on 11/05/17.
  */
case class ImplNewMultiRecordProperty[+Key, +Value: BoxedOrValidValue](
  parent: Path,
  tempId: Int,
  key: Key,
  value: Value
) extends NewMultiRecordProperty[Key, Value, ImplNewMultiRecordProperty[Key, Value]]
