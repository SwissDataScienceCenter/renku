package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.new_.impl.ImplNewRichProperty
import ch.datascience.graph.elements.persisted.Path

/**
  * Created by johann on 29/05/17.
  */
trait NewRichProperty extends NewProperty with RichProperty {

  final type Prop = DetachedProperty

}

object NewRichProperty {

  def apply(
    parent: Path,
    key: NewRichProperty#Key,
    value: NewRichProperty#Value,
    properties: NewRichProperty#Properties
  ): NewRichProperty = ImplNewRichProperty(parent, key, value, properties)

  def unapply(prop: NewRichProperty): Option[(Path, NewRichProperty#Key, NewRichProperty#Value, NewRichProperty#Properties)] = {
    if (prop eq null)
      None
    else
      Some(prop.parent, prop.key, prop.value, prop.properties)
  }

}
