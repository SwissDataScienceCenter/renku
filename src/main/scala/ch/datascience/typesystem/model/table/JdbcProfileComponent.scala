package ch.datascience.typesystem.model.table

import ch.datascience.typesystem.external.DatabaseConfigComponent
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 17/03/17.
  */
trait JdbcProfileComponent extends DatabaseConfigComponent[JdbcProfile]
