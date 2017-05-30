package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.Property
import ch.datascience.graph.elements.detached.impl.ImplDetachedLeafProperty

/**
  * Created by johann on 27/04/17.
  */
trait DetachedProperty extends Property

object DetachedProperty {

  def apply(
    key: DetachedProperty#Key,
    value: DetachedProperty#Value
  ): DetachedProperty = ImplDetachedLeafProperty(key, value)

  def unapply(prop: DetachedProperty): Option[(DetachedProperty#Key, DetachedProperty#Value)] = {
    if (prop eq null)
      None
    else
      Some(prop.key, prop.value)
  }

}
