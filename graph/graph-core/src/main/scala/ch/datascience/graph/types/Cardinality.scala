package ch.datascience.graph.types

import org.apache.tinkerpop.gremlin.structure.VertexProperty

/**
  * Created by johann on 09/04/17.
  */
sealed abstract class Cardinality(val name: String) {

  def toTinkerPop: VertexProperty.Cardinality

}

object Cardinality {

  def valueOf(name: String): Cardinality = Cardinality.apply(name)

  def apply(name: String): Cardinality = name.toLowerCase match {
    case Single.name => Single
    case List.name   => List
    case Set.name    => Set
  }

  case object Single extends Cardinality(name = "single") {
    def toTinkerPop: VertexProperty.Cardinality = VertexProperty.Cardinality.single
  }

  case object List extends Cardinality(name = "list") {
    def toTinkerPop: VertexProperty.Cardinality = VertexProperty.Cardinality.list
  }

  case object Set extends Cardinality(name = "set") {
    def toTinkerPop: VertexProperty.Cardinality = VertexProperty.Cardinality.set
  }

  def apply(card: VertexProperty.Cardinality): Cardinality = card match {
    case VertexProperty.Cardinality.single => Cardinality.Single
    case VertexProperty.Cardinality.set => Cardinality.Set
    case VertexProperty.Cardinality.list => Cardinality.List
  }

}
