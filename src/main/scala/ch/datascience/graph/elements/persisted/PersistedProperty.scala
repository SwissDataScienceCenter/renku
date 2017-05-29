package ch.datascience.graph.elements.persisted

import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements.Property

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

  final type PathType = PropertyPathFromRecord[Key]

  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)

}

trait PersistedMultiRecordProperty
  extends PersistedProperty
    with PersistedElement
    with HasId {

  final type PathType = PropertyPathFromMultiRecord[Id]

  final def path: PropertyPathFromMultiRecord[Id] = PropertyPathFromMultiRecord(parent, id)

}
