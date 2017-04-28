package ch.datascience.graph

import language.higherKinds

/**
  * Created by johann on 28/04/17.
  */
package object elements {

  type MultiProperties[Key, Value, Holder[V] <: HasValue[V, Holder]] = Map[Key, MultiPropertyValue[Value, Holder]]

}
