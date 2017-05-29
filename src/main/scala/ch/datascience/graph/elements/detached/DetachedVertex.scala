package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.{MultiPropertyValue, Vertex}
import ch.datascience.graph.values.{BoxedOrValidValue, BoxedValue}

/**
  * Created by johann on 27/04/17.
  */
trait DetachedVertex extends Vertex {

//  val types: Set[TypeId]
//
//  val properties: Properties

  final type Prop = DetachedRichProperty

}
