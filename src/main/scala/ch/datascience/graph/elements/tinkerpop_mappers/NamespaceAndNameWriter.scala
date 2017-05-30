package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.naming.NamespaceAndName

/**
  * Created by johann on 30/05/17.
  */
case object NamespaceAndNameWriter extends Writer[NamespaceAndName, String] {

  def write(x: NamespaceAndName): String = x.asString

}
