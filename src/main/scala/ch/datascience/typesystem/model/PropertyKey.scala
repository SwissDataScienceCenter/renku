package ch.datascience.typesystem
package model

import java.util.UUID

import ch.datascience.typesystem.model.base.PropertyKeyBase
import ch.datascience.typesystem.model.relational.{PropertyKeyBase => RelationalPropertyKey}


/**
  * Created by johann on 24/04/17.
  */
case class PropertyKey(
                        id: UUID,
                        graphDomain: GraphDomain,
                        name: String,
                        dataType: DataType,
                        cardinality: Cardinality
                      )
  extends RelationalPropertyKey
    with PropertyKeyBase[String] {

  lazy val namespace: String = graphDomain.namespace

  override lazy val key: String = s"$namespace:$name"

  override lazy val graphDomainId: UUID = graphDomain.id

}

object PropertyKey {

  def fromRelational(graphDomain: GraphDomain, relationalPropertyKey: RelationalPropertyKey): PropertyKey = {
    PropertyKey(relationalPropertyKey.id, graphDomain, relationalPropertyKey.name, relationalPropertyKey.dataType, relationalPropertyKey.cardinality)
  }

  def tupled: (((UUID, GraphDomain, String, DataType, Cardinality)) => PropertyKey) = (PropertyKey.apply _).tupled

}
