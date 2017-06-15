package ch.datascience.graph.elements

import ch.datascience.graph.Constants
import ch.datascience.graph.scope.PropertyScope

/**
  * Created by johann on 30/05/17.
  */
package object tinkerpop_mappers {

  lazy val KeyReader: SyncStringReader[Constants.Key] = NamespaceAndNameReader
  lazy val TypeIdReader: SyncStringReader[Constants.TypeId] = NamespaceAndNameReader
  lazy val EdgeLabelReader: SyncStringReader[Constants.EdgeLabel] = NamespaceAndNameReader

  type ValueReader = KeyValueReader[Constants.Key, Constants.Value]
  lazy val ValueReader: (PropertyScope) => ValueReader = BoxedReader

  lazy val KeyWriter: Writer[Constants.Key, String] = NamespaceAndNameWriter
  lazy val TypeIdWriter: Writer[Constants.TypeId, String] = NamespaceAndNameWriter
  lazy val EdgeLabelWriter: Writer[Constants.EdgeLabel, String] = NamespaceAndNameWriter

  type ValueWriter = Writer[Constants.Value, java.lang.Object]
  lazy val ValueWriter: ValueWriter = BoxedValueWriter

}
