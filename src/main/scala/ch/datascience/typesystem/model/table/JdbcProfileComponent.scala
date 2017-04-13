package ch.datascience.typesystem.model.table

import slick.jdbc.JdbcProfile

import scala.collection.mutable

/**
  * Created by johann on 17/03/17.
  */
trait JdbcProfileComponent {

  val profile: JdbcProfile

  type Schema = profile.SchemaDescription

  protected lazy val _schemas: mutable.Builder[Schema, Seq[Schema]] = Seq.newBuilder[Schema]
  def schemas: Seq[Schema] = _schemas.result()

}
