package ch.datascience.typesystem.model

import ch.datascience.typesystem.model.base.RecordTypeBase

/**
  * Created by johann on 26/04/17.
  */
case class RecordType(properties: Set[String]) extends RecordTypeBase[String]
