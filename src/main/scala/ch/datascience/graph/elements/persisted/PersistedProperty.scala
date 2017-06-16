package ch.datascience.graph.elements.persisted

import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements.Property
import ch.datascience.graph.elements.persisted.impl.ImplPersistedRecordLeafProperty

/**
  * Created by johann on 29/05/17.
  */
sealed trait PersistedProperty
  extends Property
    with PersistedElement {

  type PathType <: PropertyPath

  def parent: Path

}

trait PersistedRecordProperty
  extends PersistedProperty
    with PersistedElement {

  final type PathType = PropertyPathFromRecord

  final def path: PropertyPathFromRecord = PropertyPathFromRecord(parent, key)

}

trait PersistedMultiRecordProperty
  extends PersistedProperty
    with PersistedElement
    with HasId {

//  final type PathType = PropertyPathFromMultiRecord[Id]
  type PathType <: PropertyPathFromMultiRecord[Id]

//  final def path: PropertyPathFromMultiRecord[Id] = PropertyPathFromMultiRecord(parent, id)

}

object PersistedRecordProperty {

  def apply(
    parent: Path,
    key: PersistedRecordProperty#Key,
    value: PersistedRecordProperty#Value
  ): PersistedRecordProperty = ImplPersistedRecordLeafProperty(parent, key, value)

  def unapply(prop: PersistedRecordProperty): Option[(Path, PersistedRecordProperty#Key, PersistedRecordProperty#Value)] = {
    if (prop eq null)
      None
    else
      Some(prop.parent, prop.key, prop.value)
  }

}
