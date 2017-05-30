package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.naming.NamespaceAndName

/**
  * Created by johann on 19/05/17.
  */
case object NamespaceAndNameReader extends SyncStringReader[NamespaceAndName] {

  def readSync(x: String): NamespaceAndName = NamespaceAndName(x)

}
