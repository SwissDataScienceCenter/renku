package ch.datascience.graph.elements.builders

/**
  * Created by johann on 19/05/17.
  */
trait Builder[+To] {

  def isReady: Boolean

  def result(): To

  def notReady(): Nothing = throw new IllegalArgumentException("Builder is not ready")

}
