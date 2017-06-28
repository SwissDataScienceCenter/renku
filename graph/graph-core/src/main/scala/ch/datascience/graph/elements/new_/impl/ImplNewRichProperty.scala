package ch.datascience.graph.elements.new_.impl

import ch.datascience.graph.elements.new_.NewRichProperty
import ch.datascience.graph.elements.persisted.Path

/**
  * Created by johann on 11/05/17.
  */
private[new_] case class ImplNewRichProperty(
  parent: Path,
  key: NewRichProperty#Key,
  value: NewRichProperty#Value,
  properties: NewRichProperty#Properties
) extends NewRichProperty
