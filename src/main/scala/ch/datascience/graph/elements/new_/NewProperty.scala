package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Property
import ch.datascience.graph.elements.new_.impl.ImplNewLeafProperty
import ch.datascience.graph.elements.persisted.Path

/**
  * Created by johann on 29/05/17.
  */
trait NewProperty extends Property with NewElement {

  def parent: Path

}

object NewProperty {

  def apply(
    parent: Path,
    key: NewProperty#Key,
    value: NewProperty#Value
  ): NewProperty = ImplNewLeafProperty(parent, key, value)

  def unapply(prop: NewProperty): Option[(Path, NewProperty#Key, NewProperty#Value)] = {
    if (prop eq null)
      None
    else
      Some(prop.parent, prop.key, prop.value)
  }

}
