package ch.datascience.typesystem.model

import ch.datascience.typesystem.model.base.GraphObjectBase

/**
  * Created by johann on 26/04/17.
  */
case class GraphObject(types: Set[String], properties: Map[String, Any]) extends GraphObjectBase[String, String]
