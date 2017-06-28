package ch.datascience.graph.types.impl

import ch.datascience.graph.types.{Cardinality, DataType, PropertyKey}

/**
  * Created by johann on 12/05/17.
  */
private[types] final case class ImplPropertyKey(
  key: PropertyKey#Key,
  dataType: DataType,
  cardinality: Cardinality
) extends PropertyKey {

  override def toString: String = s"PropertyKey($key, $cardinality, $dataType)"

}
