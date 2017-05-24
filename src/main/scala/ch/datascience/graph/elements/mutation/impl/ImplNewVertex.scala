package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.{MultiProperties, Property, RichProperty}
import ch.datascience.graph.elements.persistence.NewVertex
import ch.datascience.graph.values.BoxedValue

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplNewVertex[
TypeId,
Key,
+Value,
+MetaValue
](
   tempId: Int,
   types: Set[TypeId],
   properties: MultiProperties[Key, Value, RichProperty[Key, Value, MetaValue, Property[Key, MetaValue]]]
 ) extends NewVertex[
  TypeId,
  Key,
  Value,
  MetaValue,
  Property[Key, MetaValue],
  RichProperty[Key, Value, MetaValue, Property[Key, MetaValue]]
  ]
