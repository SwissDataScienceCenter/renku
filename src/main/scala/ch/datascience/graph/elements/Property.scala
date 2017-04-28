package ch.datascience.graph.elements

import ch.datascience.graph.types.DataType
import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
abstract class Property[Key, Value : ValidValue, This[K, V] <: Property[K, V, This]] extends Element with HasValue[Value, PropertyHelper[Key, This]#PropertyV] {

  val key: Key

  val value: Value

  override def validValueEvidence: ValidValue[Value] = implicitly[ValidValue[Value]]

}

private[this] class PropertyHelper[Key, This[K, V] <: Property[K, V, This]] {
  type PropertyV[V] = This[Key, V]
}
