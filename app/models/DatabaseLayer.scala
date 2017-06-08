package models

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.mutation.log.db.DatabaseStack
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class DatabaseLayer @Inject()(
  @NamedDatabase("default") protected val dbConfigProvider : DatabaseConfigProvider
) extends DatabaseStack(
  dbConfig = dbConfigProvider.get
)
