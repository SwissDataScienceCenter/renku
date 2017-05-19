package ch.datascience.graph.elements.builders

import ch.datascience.graph.elements.{Property, MultiRecord}

/**
  * Created by johann on 19/05/17.
  */
trait MultiRecordBuilder[Key, Value, +Prop <: Property[Key, Value, Prop], +PB <: PropertyBuilder[Key, Value, Prop], +To <: MultiRecord[Key, Value, Prop]]
  extends Builder[To] {

  private[this] var myProperties: Map[Key, Seq[PB]] = Map.empty

  def properties: Map[Key, Seq[PB]] = myProperties

  def newProperty: Builder[PB]

  def +=(keyValue: (Key, Value)): this.type = {
    addProperty(keyValue._1, keyValue._2)
  }

  def addProperty(key: Key, value: Value): this.type = {
    val propertyBuilder = newProperty.result()
    propertyBuilder.key = key
    propertyBuilder.value = value
    val seq = properties get key match {
      case Some(s) => s :+ propertyBuilder
      case None => Seq(propertyBuilder)
    }
    myProperties += key -> seq
    this
  }

}
