package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedValue, MultiProperties, Vertex}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleVertex[TypeId, Key, MetaKey](
  types     : Set[TypeId],
  properties: MultiProperties[Key, BoxedValue, SimpleRichProperty[Key, BoxedValue, MetaKey, BoxedValue]]
) extends Vertex[TypeId, Key, BoxedValue, MetaKey, BoxedValue, SimpleProperty[MetaKey, BoxedValue],
  SimpleRichProperty[Key, BoxedValue, MetaKey, BoxedValue]]
