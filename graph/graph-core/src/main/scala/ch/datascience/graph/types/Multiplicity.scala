package ch.datascience.graph.types

/**
  * Created by johann on 07/06/17.
  */
sealed abstract class Multiplicity(val name: String) {
  def janusName: String
}

object Multiplicity {

  def valueOf(name: String): Multiplicity = Multiplicity.apply(name)

  def apply(name: String): Multiplicity = name.toLowerCase match {
    case Multi.name     => Multi
    case Simple.name    => Simple
    case OneToMany.name => OneToMany
    case ManyToOne.name => ManyToOne
    case OneToOne.name  => OneToOne
  }

  case object Multi extends Multiplicity(name = "multi") {
    def janusName: String = name
  }

  case object Simple extends Multiplicity(name = "simple") {
    def janusName: String = name
  }

  case object OneToMany extends Multiplicity(name = "one_to_many") {
    def janusName: String = "one2many"
  }

  case object ManyToOne extends Multiplicity(name = "many_to_one") {
    def janusName: String = "many2one"
  }

  case object OneToOne extends Multiplicity(name = "one_to_one") {
    def janusName: String = "one2one"
  }

}
