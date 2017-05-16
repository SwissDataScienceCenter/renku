package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.NamespaceAndName
import ch.datascience.graph.types.persistence.model.relational.RowGraphDomain

/**
  * Created by johann on 15/03/17.
  */
case class GraphDomain(id: UUID, namespace: String) extends AbstractEntity[RowGraphDomain] {
  require(
    NamespaceAndName.namespaceIsValid(namespace),
    s"Invalid namespace: '$namespace' (Pattern: ${NamespaceAndName.namespacePattern})"
  )

  def toRow: RowGraphDomain = RowGraphDomain(id, namespace)

  final override val entityType: EntityType = EntityType.GraphDomain

}

//object GraphDomain {
//
//  def make(rowGraphDomain: RowGraphDomain): GraphDomain = GraphDomain(rowGraphDomain.id, rowGraphDomain.namespace)
//
//  def tupled: ((UUID, String)) => GraphDomain = (GraphDomain.apply _).tupled
//
//}
