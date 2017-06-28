package models

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.mutation.log.dao.{ResponseDAO => Base}
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class ResponseDAO @Inject()(
  @NamedDatabase("default") protected val dbConfigProvider : DatabaseConfigProvider,
  override protected val dal: DatabaseLayer
) extends Base(
  ec = play.api.libs.concurrent.Execution.defaultContext,
  dbConfig = dbConfigProvider.get,
  dal = dal
)
