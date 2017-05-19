package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements.{BoxedOrValidValue, Property}
import ch.datascience.graph.elements.persistence.impl.ImplPersistedRecordProperty

/**
  * Created by johann on 18/05/17.
  */
class PersistedRecordPropertyBuilder[+Key, +Value : BoxedOrValidValue](val key: Key, val value: Value) {

  def result(parent: Path): ImplPersistedRecordProperty[Key, Value] = ImplPersistedRecordProperty(parent, key, value)

}
