package ch.datascience.graph.init

import ch.datascience.graph.Constants
import ch.datascience.graph.types.{EdgeLabel, NamedType, PropertyKey}

/**
  * Created by johann on 21/06/17.
  */
case class TypeInit(
  systemPropertyKeys: List[SystemPropertyKey],
  propertyKeys: List[PropertyKey],
  namedTypes: List[NamedType],
  edgeLabels: List[EdgeLabel]
) {

  def graphDomains: Seq[String] = {
    val gd1 = (for {
      pk <- propertyKeys
    } yield pk.key.namespace).toSet

    val gd2 = (for {
      nt <- namedTypes
    } yield nt.typeId.namespace).toSet

    val gd3 = (for {
      el <- edgeLabels
    } yield el.key.namespace).toSet

    (gd1 ++ gd2 ++ gd3).toSeq
  }

}
