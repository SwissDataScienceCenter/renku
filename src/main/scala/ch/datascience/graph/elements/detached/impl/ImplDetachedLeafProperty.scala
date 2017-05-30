package ch.datascience.graph.elements.detached.impl

import ch.datascience.graph.elements.detached.DetachedProperty

/**
  * Created by johann on 29/05/17.
  */
private[detached] case class ImplDetachedLeafProperty(
  key: DetachedProperty#Key,
  value: DetachedProperty#Value
) extends DetachedProperty
