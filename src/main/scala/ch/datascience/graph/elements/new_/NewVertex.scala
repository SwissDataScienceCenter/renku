package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Vertex

/**
  * Created by johann on 29/05/17.
  */
trait NewVertex extends Vertex with NewElement {

  type TempId = Int

  def tempId: TempId

}
