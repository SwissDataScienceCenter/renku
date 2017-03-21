package ch.datascience.typesystem.model.table

import slick.jdbc.JdbcProfile

/**
  * Created by johann on 17/03/17.
  */
trait JdbcProfileComponent {

  val profile: JdbcProfile

}
