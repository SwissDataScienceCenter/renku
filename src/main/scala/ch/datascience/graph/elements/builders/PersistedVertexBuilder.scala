package ch.datascience.graph.elements.builders

import ch.datascience.graph.elements._
import ch.datascience.graph.elements.persistence.PersistedVertex
import ch.datascience.graph.elements.persistence.impl.{ImplPersistedMultiRecordRichProperty, ImplPersistedVertex}
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.Cardinality

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 18/05/17.
  */
class PersistedVertexBuilder[Id, TypeId, Key, Value: BoxedOrValidValue, MetaKey, MetaValue: BoxedOrValidValue, PropId]
  extends MultiRecordBuilder[Key, Value, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue],  PersistedMultiRecordRichPropertyBuilder[PropId, Key, Value, MetaKey, MetaValue], ImplPersistedVertex[Id, TypeId, Key, Value, MetaKey, MetaValue, PropId]] {

  object newProperty extends Builder[PersistedMultiRecordRichPropertyBuilder[PropId, Key, Value, MetaKey, MetaValue]] {
    override def isReady: Boolean = true
    override def result(): PersistedMultiRecordRichPropertyBuilder[PropId, Key, Value, MetaKey, MetaValue] = new PersistedMultiRecordRichPropertyBuilder()
  }

  private[this] var myId: Option[Id] = None

  private[this] var myTypes: Set[TypeId] = Set.empty

  private[this] var myCardinalities: Map[Key, Cardinality] = Map.empty

  def id: Option[Id] = myId

  def id_=(id: Id): this.type = {
    myId = Some(id)
    this
  }

  def types: Set[TypeId] = myTypes

  def addType(typeId: TypeId): this.type = {
    myTypes += typeId
    this
  }

  def cardinalities: Map[Key, Cardinality] = myCardinalities

  def cardinalities_=(cardinalities: Map[Key, Cardinality]): this.type = {
    myCardinalities = cardinalities
    this
  }

  def isReady: Boolean = id.isDefined

  def result(): ImplPersistedVertex[Id, TypeId, Key, Value, MetaKey, MetaValue, PropId] = id match {
    case Some(i) =>
      val temp = ImplPersistedVertex(id, Set.empty, Map.empty[Key, MultiPropertyValue[Key, Value, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue]]])
      val thisPath = temp.path

      type Prop = ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue]
      val propertiesResult = for {
        (key, seq) <- properties
      } yield key -> {
        val cardinality = cardinalities.getOrElse(key, Cardinality.Single)
        val propertyValues = seq map { propertyBuilder => propertyBuilder.parent = thisPath; propertyBuilder.result() }
        cardinality match {
          case Cardinality.Single => SingleValue[Key, Value, Prop](propertyValues.head)
          case Cardinality.Set => SetValue[Key, Value, Prop](propertyValues.toList)
          case Cardinality.List => ListValue[Key, Value, Prop](propertyValues.toList)
        }
      }

      ImplPersistedVertex(i, types, propertiesResult)
    case _ => notReady()
  }

}
