package ch.datascience.graph.elements

/**
  * Created by jeberle on 15.05.17.
  */
trait Edge extends TypedRecord {

  type VertexReference

  /**
    * The origin Vertex
    *
    * @return the vertex
    */
  def from: VertexReference

  /**
    * The destination Vertex
    *
    * @return the vertex
    */
  def to: VertexReference

}
