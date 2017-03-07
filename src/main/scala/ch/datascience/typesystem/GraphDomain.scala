package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
trait GraphDomain {

  def authority: Authority

  def namespace: String

  def version: Int

  def edgeLabels: Map[String, GraphDomain.EdgeLabel]

  def propertyKeys: Map[String, GraphDomain.PropertyKey]

  def constraints: Set[Constraint]


}

object GraphDomain {

  case class EdgeLabel(name: String, version: String)

  case class PropertyKey(name: String, version:String, cardinality: Cardinality, dataType: DataType)

  def builder(): GraphDomainBuilder = new GraphDomainBuilder()

}
