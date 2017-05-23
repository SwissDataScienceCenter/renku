package ch.datascience.graph.types.persistence.model

import java.util.UUID

/**
  * Created by johann on 12/05/17.
  */
case class NamedTypeLink(namedTypeId: UUID, parentNamedTypeId: UUID) extends Row
