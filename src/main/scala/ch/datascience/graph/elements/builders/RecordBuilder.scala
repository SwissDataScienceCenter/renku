package ch.datascience.graph.elements.builders

import ch.datascience.graph.elements.{Properties, Property, Record}

/**
  * Created by johann on 19/05/17.
  */
trait RecordBuilder[Key, Value, +Prop <: Property[Key, Value, Prop], +PB <: PropertyBuilder[Key, Value, Prop], +To <: Record[Key, Value, Prop]]
  extends Builder[To] {

  private[this] var myProperties: Map[Key, PB] = Map.empty

  def properties: Map[Key, PB] = myProperties

  def newProperty: Builder[PB]

  def +=(keyValue: (Key, Value)): this.type = {
    val propertyBuilder = newProperty.result()
    propertyBuilder.key = keyValue._1
    propertyBuilder.value = keyValue._2
    myProperties += keyValue._1 -> propertyBuilder
    this
  }

}
