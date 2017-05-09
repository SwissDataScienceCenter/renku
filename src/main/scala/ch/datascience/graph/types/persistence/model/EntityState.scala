package ch.datascience.graph.types.persistence.model

/**
  * Created by johann on 14/04/17.
  */
sealed abstract class EntityState(val name: String)

object EntityState {

  def apply(name: String): EntityState = name.toLowerCase match {
    case Pending.name => Pending
    case Disabled.name => Disabled
    case Enabled.name => Enabled
    case Failed.name => Failed
  }

  case object Pending extends EntityState(name = "pending")

  case object Disabled extends EntityState(name = "disabled")

  case object Enabled extends EntityState(name = "enabled")

  case object Failed extends EntityState(name = "failed")

  def valueOf(name: String): EntityState = EntityState.apply(name)

}
