package ch.datascience
package graph
package elements

import ch.datascience.graph.types.{GraphType, RecordType}

/**
  * Base trait for records, i.e. elements that hold properties
  *
  * Properties can be validated (see package types).
  *
  * @tparam Key   key type
  * @tparam Value value type
  * @tparam Prop  property type
  */
trait Record[Key, +Value, +Prop <: Property[Key, Value]] extends Element {

  def properties: Properties[Key, Value, Prop]

  protected[elements] def <|(graphType: GraphType): Boolean = graphType match {
    case rt: RecordType[Key] => rt.properties subsetOf properties.keySet
    case _ => false
  }

}
