package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{Property, ValidValue}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleProperty[Key, Value : ValidValue](
    override val key: Key,
    override val value: Value
) extends Property[Key, Value] {

  override def validValueEvidence: ValidValue[Value] = implicitly[ValidValue[Value]]

}
