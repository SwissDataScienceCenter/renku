package ch.datascience.graph.init

import ch.datascience.graph.types.{Cardinality, DataType}

case class SystemPropertyKey(name: String, dataType: DataType, cardinality: Cardinality)
