package ch.datascience.graph.elements.json

/**
  * Created by johann on 24/05/17.
  */
trait StringFormat[Key] extends StringReads[Key] with StringWrites[Key]
