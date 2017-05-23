package ch.datascience.graph.types

/**
  * Created by johann on 23/05/17.
  */
object StandardPropertyKey {

  def apply(key: StandardPropKey, dataType: DataType, cardinality: Cardinality): StandardPropertyKey = {
    PropertyKey.apply[StandardPropKey](key, dataType, cardinality)
  }

  def unapply(propertyKey: StandardPropertyKey): Option[(StandardPropKey, DataType, Cardinality)] = {
    PropertyKey.unapply[StandardPropKey](propertyKey)
  }

}
