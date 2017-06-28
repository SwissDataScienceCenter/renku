package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.types.{PropertyKey => StandardPropertyKey}

/**
  * Created by johann on 23/05/17.
  */
class RichPropertyKey(
  id: UUID,
  val graphDomain: GraphDomain,
  name: String,
  dataType: DataType,
  cardinality: Cardinality
) extends PropertyKey(
  id,
  graphDomain.id,
  name,
  dataType,
  cardinality
) with RichAbstractEntity[PropertyKey] {

  def key: NamespaceAndName = NamespaceAndName(graphDomain.namespace, name)

  def toStandardPropertyKey: StandardPropertyKey = StandardPropertyKey(key, dataType, cardinality)

}

object RichPropertyKey {

  def apply(id: UUID, graphDomain: GraphDomain, name: String, dataType: DataType, cardinality: Cardinality): RichPropertyKey = {
    new RichPropertyKey(id, graphDomain, name, dataType, cardinality)
  }

  def unapply(propertyKey: RichPropertyKey): Option[(UUID, GraphDomain, String, DataType, Cardinality)] = {
    if (propertyKey eq null)
      None
    else
      Some((propertyKey.id, propertyKey.graphDomain, propertyKey.name, propertyKey.dataType, propertyKey.cardinality))
  }

}
