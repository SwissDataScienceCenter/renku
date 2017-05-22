package ch.datascience.graph.elements

/**
  * Created by jeberle on 15.05.17.
  */
trait Edge[Key, +Value, +EdgeProp <: Property[Key, Value], +VRef] extends Record[Key, Value, EdgeProp] {

  /**
    * The origin Vertex
    *
    * @return the vertex
    */
  def from: VRef

  /**
    * The destination Vertex
    *
    * @return the vertex
    */
  def to: VRef

}
