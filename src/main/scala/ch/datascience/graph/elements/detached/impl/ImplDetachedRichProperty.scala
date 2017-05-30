package ch.datascience.graph.elements.detached.impl

import ch.datascience.graph.elements.detached.DetachedRichProperty

/**
  * Created by johann on 29/05/17.
  */
private[detached] case class ImplDetachedRichProperty(
  key: DetachedRichProperty#Key,
  value: DetachedRichProperty#Value,
  properties: DetachedRichProperty#Properties
) extends DetachedRichProperty
