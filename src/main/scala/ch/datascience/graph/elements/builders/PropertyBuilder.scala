package ch.datascience.graph.elements.builders

import ch.datascience.graph.elements.Property

/**
  * Created by johann on 19/05/17.
  */
trait PropertyBuilder[Key, Value, +Prop <: Property[Key, Value, Prop]] extends Builder[Prop] {

  private[this] var myKey: Option[Key] = None

  private[this] var myValue: Option[Value] = None

  def key: Option[Key] = myKey

  def value: Option[Value] = myValue

  def key_=(key: Key): this.type = {
    myKey = Some(key)
    this
  }

  def value_=(value: Value): this.type = {
    myValue = Some(value)
    this
  }

}
