package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements._
import ch.datascience.graph.elements.persistence.impl.{ImplPersistedMultiRecordRichProperty, ImplPersistedRecordProperty, ImplPersistedVertex}
import ch.datascience.graph.types.{Cardinality, PropertyKey}

/**
  * Created by johann on 18/05/17.
  */
class PersistedVertexBuilder[
+Id,
TypeId,
Key,
Value: BoxedOrValidValue,
MetaKey,
MetaValue: BoxedOrValidValue,
PropId
](val id: Id) {

  private[this] var myTypes: Set[TypeId] = Set.empty

  private[this] var myProperties: Map[Key, Map[PropId, PersistedMultiRecordRichPropertyBuilder[PropId, Key, Value, MetaKey, MetaValue]]] = Map.empty

  def types: Set[TypeId] = myTypes

  def properties: Map[Key, Map[PropId, PersistedMultiRecordRichPropertyBuilder[PropId, Key, Value, MetaKey, MetaValue]]] = myProperties

  def +=(propIdKeyValue: (PropId, (Key, Value))): this.type = addProperty(propIdKeyValue._1, propIdKeyValue._2._1, propIdKeyValue._2._2)

  def addType(typeId: TypeId): this.type = {
    myTypes += typeId
    this
  }

  def addProperty(propId: PropId, key: Key, value: Value): this.type = {
    val builder = new PersistedMultiRecordRichPropertyBuilder[PropId, Key, Value, MetaKey, MetaValue](propId, key, value)
    val subMap = myProperties get key match {
      case Some(map) => map + (propId -> builder)
      case None => Map(propId -> builder)
    }
    myProperties += key -> subMap
    this
  }

  def result(cardinalities: Map[Key, Cardinality]): ImplPersistedVertex[Id, TypeId, Key, Value, MetaKey, MetaValue, PropId] = {
    val temp = ImplPersistedVertex(id, Set.empty, Map.empty[Key, MultiPropertyValue[Key, Value, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue]]])
    val thisPath = temp.path

    type Prop = ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue]
    val properties = for {
      (key, subMap) <- myProperties
    } yield key -> {
      val cardinality = cardinalities.getOrElse(key, Cardinality.Single)
      val propertyValues = subMap.values.map(_.result(thisPath))
      cardinality match {
        case Cardinality.Single => SingleValue[Key, Value, Prop](propertyValues.head)
        case Cardinality.Set => SetValue[Key, Value, Prop](propertyValues.toList)
        case Cardinality.List => ListValue[Key, Value, Prop](propertyValues.toList)
      }
    }

    ImplPersistedVertex(id, myTypes, properties)
  }

}
