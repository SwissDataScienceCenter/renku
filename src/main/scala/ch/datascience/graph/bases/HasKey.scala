package ch.datascience.graph.bases

/**
  * Base trait for things that have a key
  */
trait HasKey {

  /**
    * Key type of key
    */
  type Key

  /**
    * @return the key
    */
  def key: Key

}
