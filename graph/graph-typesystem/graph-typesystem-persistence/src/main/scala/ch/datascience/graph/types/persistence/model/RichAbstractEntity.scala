package ch.datascience.graph.types.persistence.model

/**
  * Created by johann on 23/05/17.
  */
trait RichAbstractEntity[+A <: AbstractEntity] extends AbstractEntity {
  this: A =>

  def unlifted: A = this

}
