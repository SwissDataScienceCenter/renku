package ch.datascience.graph.elements

import ch.datascience.graph.HasKey

/**
  * Created by johann on 30/04/17.
  */
trait PropertyBase[+Key, +Value] extends HasKey[Key] with HasValueBase[Value]
