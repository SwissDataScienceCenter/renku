package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
class GraphDomainBuilder(private val _authority: Option[Authority],
                         private val _namespace: Option[String],
                         private val _version: Int,
                         private val _edgeLabels: Map[String, GraphDomain.EdgeLabel],
                         private val _propertyKeys: Map[String, GraphDomain.PropertyKey],
                         private val _constraints: Set[Constraint]) {

  def this() {
    this(None, None, 0, Map(), Map(), Set())
  }

  def authority(authority: Authority): GraphDomainBuilder = {
    new GraphDomainBuilder(Some(authority), _namespace, _version, _edgeLabels, _propertyKeys, _constraints)
  }

  def namespace(namespace: String): GraphDomainBuilder = {
    new GraphDomainBuilder(_authority, Some(namespace), _version, _edgeLabels, _propertyKeys, _constraints)
  }

  def version(version: Int): GraphDomainBuilder = {
    new GraphDomainBuilder(_authority, _namespace, version, _edgeLabels, _propertyKeys, _constraints)
  }

  def addEdgeLabel(label: GraphDomain.EdgeLabel): GraphDomainBuilder = {
    val key = s"${label.name}:${label.version}"
    new GraphDomainBuilder(_authority, _namespace, _version, _edgeLabels + (key -> label), _propertyKeys, _constraints)
  }

  def addPropertyKey(propertyKey: GraphDomain.PropertyKey): GraphDomainBuilder = {
    val key = s"${propertyKey.name}:${propertyKey.version}"
    new GraphDomainBuilder(_authority, _namespace, _version, _edgeLabels, _propertyKeys + (key -> propertyKey), _constraints)
  }

  def addConstraint(constraint: Constraint): GraphDomainBuilder = {
    new GraphDomainBuilder(_authority, _namespace, _version, _edgeLabels, _propertyKeys, _constraints + constraint)
  }

  def make(): GraphDomain = {
    if (_authority.isEmpty) throw new IllegalArgumentException("authority must be provided")
    if (_namespace.isEmpty) throw new IllegalArgumentException("namespace must be provided")

    GraphDomainImpl(_authority.get, _namespace.get, _version, _edgeLabels, _propertyKeys, _constraints)
  }

  case class GraphDomainImpl(authority: Authority,
                             namespace: String,
                             version: Int,
                             edgeLabels: Map[String, GraphDomain.EdgeLabel],
                             propertyKeys: Map[String, GraphDomain.PropertyKey],
                             constraints: Set[Constraint]) extends GraphDomain


  }
