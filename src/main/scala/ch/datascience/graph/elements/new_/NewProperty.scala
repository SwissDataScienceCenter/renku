package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Property
import ch.datascience.graph.elements.persisted.Path

/**
  * Created by johann on 29/05/17.
  */
trait NewProperty extends Property with NewElement {

  def parent: Path

}
