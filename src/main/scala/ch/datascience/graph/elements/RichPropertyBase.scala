package ch.datascience.graph.elements

/**
  * Basic trait for rich property
  *
  * @tparam Key       key type
  * @tparam Value     value type
  * @tparam MetaKey   meta-key type
  * @tparam MetaValue meta-value-type
  * @tparam MetaProp  meta-property type
  */
trait RichPropertyBase[+Key, +Value, MetaKey, +MetaValue, +MetaProp <: Property[MetaKey, MetaValue, MetaProp]]
  extends PropertyBase[Key, Value]
    with Record[MetaKey, MetaValue, MetaProp]
