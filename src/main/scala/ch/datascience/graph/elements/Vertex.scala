package ch.datascience.graph.elements

import scala.language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
trait Vertex[
TypeId,
Key,
+Value,
MetaKey,
+MetaValue,
+MetaProp <: Property[MetaKey, MetaValue, MetaProp],
+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]
] extends TypedMultiRecord[TypeId, Key, Value, Prop]


trait VertexRef[+Id]