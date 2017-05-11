package ch.datascience.graph.elements.concrete

import ch.datascience.graph.elements.{BoxedValue, HasId, MultiProperties, Vertex}

/**
  * Created by julien on 10/05/17.
  */
abstract case class AbstractVertex[TypeId, Key, MetaKey, +TemporaryId, +RealId]()
  extends Vertex[TypeId, Key, BoxedValue, MetaKey, BoxedValue, ConcreteProperty[MetaKey, BoxedValue],
  ConcreteRichProperty[Key, BoxedValue, MetaKey, BoxedValue]]
  with HasId[Either[TemporaryId, RealId]]


final case class NewVertex[TypeId, Key, MetaKey, +TemporaryId](
  types     : Set[TypeId],
  properties: MultiProperties[Key, BoxedValue, ConcreteRichProperty[Key, BoxedValue, MetaKey, BoxedValue]],
  id        : Either[TemporaryId, Nothing]
) extends AbstractVertex[TypeId, Key, MetaKey, TemporaryId, Nothing]


final case class PersistedVertex[TypeId, Key, MetaKey, +RealId](
  types     : Set[TypeId],
  properties: MultiProperties[Key, BoxedValue, ConcreteRichProperty[Key, BoxedValue, MetaKey, BoxedValue]],
  id        : Either[Nothing, RealId]
) extends AbstractVertex[TypeId, Key, MetaKey, Nothing, RealId]
