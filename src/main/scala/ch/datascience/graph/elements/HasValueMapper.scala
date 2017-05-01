package ch.datascience.graph.elements

/**
  * Base trait for enabling map operations from [[HasValue]]
  *
  * See [[ch.datascience.graph.elements.simple.SimpleProperty.Mapper]] for an example.
  *
  * @tparam From
  * @tparam FromHV
  * @tparam To
  * @tparam ToHV
  */
trait HasValueMapper[+From, -FromHV, -To, +ToHV] {

  def map(hasValue: FromHV)(f: (From) => To): ToHV

}
