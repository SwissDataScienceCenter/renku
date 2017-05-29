package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.{Edge, Vertex}

/**
  * Created by johann on 29/05/17.
  */
trait DetachedEdge extends Edge {

  final type VertexReference = Vertex

  final type Prop = DetachedProperty

//  val from: VertexReference
//
//  val to: VertexReference
//
//  val types: Set[TypeId]
//
//  val properties: Properties

}
