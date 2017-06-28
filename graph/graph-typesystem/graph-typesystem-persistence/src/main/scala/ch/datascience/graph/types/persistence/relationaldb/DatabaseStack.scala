package ch.datascience.graph.types.persistence.relationaldb

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 13/04/17.
  */
class DatabaseStack(protected val dbConfig: DatabaseConfig[JdbcProfile])
  extends JdbcProfileComponent
    with SchemasComponent
    with ImplicitsComponent
    with EntityComponent
    with StateComponent
    with TransitionComponent
    with AbstractEntityComponent
    with GraphDomainComponent
    with PropertyKeyComponent
    with NamedTypeComponent
    with EdgeLabelComponent
    with SystemPropertyKeyComponent
