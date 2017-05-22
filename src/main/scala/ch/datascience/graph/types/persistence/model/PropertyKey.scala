package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.naming.{Name, NamespaceAndName}
import ch.datascience.graph.types.persistence.model.relational.{RowGraphDomain, RowPropertyKey}
import ch.datascience.graph.types.{Cardinality, DataType, PropertyKey => PropertyKeyBase}

/**
  * Created by johann on 09/05/17.
  */
case class PropertyKey(
  id: UUID,
  graphDomain: GraphDomain,
  name: String,
  dataType: DataType,
  cardinality: Cardinality
) extends AbstractEntity[RowPropertyKey]
  with PropertyKeyBase[NamespaceAndName] {
  Name(name)
//  require(NamespaceAndName.nameIsValid(name), s"Invalid name: '$name' (Pattern: ${NamespaceAndName.namePattern})")

  def namespace: String = graphDomain.namespace

  def key: NamespaceAndName = NamespaceAndName(namespace, name)

  def toRow: RowPropertyKey = RowPropertyKey(id, graphDomain.id, name, dataType, cardinality)

  final override val entityType: EntityType = EntityType.PropertyKey

}

//object PropertyKey {
//
//  def make(rowGraphDomain: RowGraphDomain, rowPropertyKey: RowPropertyKey): PropertyKey = {
//    val graphDomain = GraphDomain.make(rowGraphDomain)
//    PropertyKey(rowPropertyKey.id, graphDomain, rowPropertyKey.name, rowPropertyKey.dataType, rowPropertyKey.cardinality)
//  }
//
//  def tupled: ((UUID, GraphDomain, String, DataType, Cardinality)) => PropertyKey = (PropertyKey.apply _).tupled
//
//}
