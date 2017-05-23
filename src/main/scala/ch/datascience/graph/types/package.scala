package ch.datascience.graph

import ch.datascience.graph.naming.NamespaceAndName

/**
  * Created by johann on 23/05/17.
  */
package object types {

  type StandardTypeKey = NamespaceAndName

  type StandardPropKey = NamespaceAndName

  type StandardPropertyKey = PropertyKey[StandardPropKey]

}
