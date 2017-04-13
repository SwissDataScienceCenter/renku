package ch.datascience.typesystem.model.table

import slick.jdbc.JdbcProfile

/**
  * Created by johann on 13/04/17.
  */
abstract class DatabaseStack extends EntityComponent with StateComponent with TransitionComponent
  with AbstractEntityComponent with GraphDomainComponent with PropertyKeyComponent with JdbcProfileComponent
