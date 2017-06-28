package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.detached.impl.ImplDetachedRichProperty


/**
  * Created by johann on 28/04/17.
  */
trait DetachedRichProperty extends DetachedProperty with RichProperty {

  final type Prop = DetachedProperty

}

object DetachedRichProperty {

  def apply(
    key: DetachedRichProperty#Key,
    value: DetachedRichProperty#Value,
    properties: DetachedRichProperty#Properties
  ): DetachedRichProperty = ImplDetachedRichProperty(key, value, properties)

  def unapply(prop: DetachedRichProperty): Option[(DetachedRichProperty#Key, DetachedRichProperty#Value, DetachedRichProperty#Properties)] = {
    if (prop eq null)
      None
    else
      Some(prop.key, prop.value, prop.properties)
  }

}
