package ch.datascience.graph.bases

/**
  * Base trait for things that have a key
  *
  * @tparam Key type of key
  */
trait HasKey[+Key] {

  /**
    * @return the key
    */
  def key: Key

}
