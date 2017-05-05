package ch.datascience.graph.typecheck

import ch.datascience.graph.elements.Property

/**
  * Created by johann on 04/05/17.
  */
class Checker[Key, Value, Prop <: Property[Key, Value, Prop]]
  extends PropertyChecker[Key, Value, Prop]
    with RecordChecker[Key, Value, Prop]
