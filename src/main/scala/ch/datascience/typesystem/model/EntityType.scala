package ch.datascience.typesystem
package model

/**
  * Created by johann on 14/04/17.
  */
sealed abstract class EntityType(val name: String)

object EntityType {

  def apply(name: String): EntityType = name.toLowerCase match {
    case GraphDomain.name => GraphDomain
    case PropertyKey.name => PropertyKey
    case EdgeLabel.name => EdgeLabel
  }

  case object GraphDomain extends EntityType(name = "graph_domain")

  case object PropertyKey extends EntityType(name = "property_key")

  case object EdgeLabel extends EntityType(name = "edge_label")

  def valueOf(name: String): EntityType = EntityType.apply(name)

}
