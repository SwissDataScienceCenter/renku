package ch.datascience.graph.elements.new_.impl

import ch.datascience.graph.elements.new_.NewProperty
import ch.datascience.graph.elements.persisted.Path

/**
  * Created by johann on 11/05/17.
  */
private[new_] case class ImplNewLeafProperty(
  parent: Path,
  key: NewProperty#Key,
  value: NewProperty#Value
) extends NewProperty
