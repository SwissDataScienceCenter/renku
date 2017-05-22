package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.Property
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleProperty[+Key, +Value: BoxedOrValidValue](key: Key, value: Value)
  extends Property[Key, Value]
