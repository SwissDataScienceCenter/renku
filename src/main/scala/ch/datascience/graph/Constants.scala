package ch.datascience.graph

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.BoxedValue

/**
  * Created by johann on 24/05/17.
  */
object Constants {

  /**
    * Type of property keys
    */
//  type StandardKey = NamespaceAndName
  type Key = NamespaceAndName

  /**
    * Type of named type identifiers
    */
//  type StandardTypeId = NamespaceAndName
  type TypeId = NamespaceAndName

  /**
    * Type of property values
    */
//  type StandardValue = BoxedValue
  type Value = BoxedValue

  /**
    * Type of edge labels
    */
  type EdgeLabel = NamespaceAndName

  /**
    * Type of vertex ids
    */
  type VertexId = Long

  /**
    * Type of edge ids
    */
  type EdgeId = String

  /**
    * Type of vertex properties
    */
  type VertexPropertyId = String

  lazy val TypeKey: String = "type"

}
