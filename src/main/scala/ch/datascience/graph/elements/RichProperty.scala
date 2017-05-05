package ch.datascience.graph.elements

import scala.language.{higherKinds, implicitConversions}

/**
  *
  * @tparam Key       key type
  * @tparam Value     value type
  * @tparam MetaKey   meta-key type
  * @tparam MetaValue meta-value type
  * @tparam MetaProp  meta-property type
  * @tparam This      self type
  */
trait RichProperty[+Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp],
+This <: RichPropertyBase[Key, Value, MetaKey, MetaValue, MetaProp]]
  extends RichPropertyBase[Key, Value, MetaKey, MetaValue, MetaProp]
    with Property[Key, Value, This]
    with Element {
  this: This =>
}
