package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.detached.DetachedProperty

/**
  * Created by johann on 29/05/17.
  */
trait NewRichProperty extends NewProperty with RichProperty {

  type Prop <: DetachedProperty

}
