package ch.datascience.graph.elements

import ch.datascience.graph.Constants

/**
  * Created by jeberle on 15.05.17.
  */
trait Edge extends Record {

  type Label = Constants.EdgeLabel

  type VertexReference

  /**
    * The edge label
    *
    * @return the label
    */
  def label: Label

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
