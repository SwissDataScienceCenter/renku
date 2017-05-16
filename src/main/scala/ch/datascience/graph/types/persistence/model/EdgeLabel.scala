package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.NamespaceAndName
import ch.datascience.graph.types.persistence.model.relational.{RowEdgeLabel, RowGraphDomain}
import org.janusgraph.core.Multiplicity

/**
  * Created by johann on 16/03/17.
  */
case class EdgeLabel(id: UUID,
                     graphDomain: GraphDomain,
                     name: String,
                     multiplicity: Multiplicity = Multiplicity.SIMPLE)
  extends AbstractEntity[RowEdgeLabel] {
  require(NamespaceAndName.nameIsValid(name), s"Invalid name: '$name' (Pattern: ${NamespaceAndName.namePattern})")

  def namespace: String = graphDomain.namespace

  def key: NamespaceAndName = NamespaceAndName(namespace, name)

  def toRow: RowEdgeLabel = RowEdgeLabel(id, graphDomain.id, name, multiplicity)

  final override val entityType: EntityType = EntityType.EdgeLabel

}

//object EdgeLabel {
//
//  def make(rowGraphDomain: RowGraphDomain, rowEdgeLabel: RowEdgeLabel): EdgeLabel = {
//    val graphDomain = GraphDomain.make(rowGraphDomain)
//    EdgeLabel(rowEdgeLabel.id, graphDomain, rowEdgeLabel.name, rowEdgeLabel.multiplicity)
//  }
//
//  def tupled: ((UUID, GraphDomain, String, Multiplicity)) => EdgeLabel = (EdgeLabel.apply _).tupled
//
//}
