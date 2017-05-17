package ch.datascience.graph.types.impl

import ch.datascience.graph.types.{Cardinality, DataType, PropertyKey}

/**
  * Created by johann on 12/05/17.
  */
private[types] final case class ImplPropertyKey[+Key](
  key: Key,
  dataType: DataType,
  cardinality: Cardinality
) extends PropertyKey[Key] {

  override def toString: String = s"PropertyKey($key, $cardinality, $dataType)"

}
