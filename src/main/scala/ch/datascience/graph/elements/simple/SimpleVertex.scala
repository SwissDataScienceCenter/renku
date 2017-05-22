package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{MultiProperties, Vertex}
import ch.datascience.graph.values.BoxedValue

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleVertex[TypeId, Key](
  types     : Set[TypeId],
  properties: MultiProperties[Key, BoxedValue, SimpleRichProperty[Key, BoxedValue, BoxedValue]]
) extends Vertex[TypeId, Key, BoxedValue, BoxedValue, SimpleProperty[Key, BoxedValue],
  SimpleRichProperty[Key, BoxedValue, BoxedValue]]
