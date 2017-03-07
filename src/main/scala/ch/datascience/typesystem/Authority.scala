package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
trait Authority {

  def name: String

}

object Authority {

  def builder(): AuthorityBuilder = new AuthorityBuilder()

}
