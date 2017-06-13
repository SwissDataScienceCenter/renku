//package ch.datascience.graph.types.persistence
//
//import slick.basic.DatabaseConfig
//import slick.jdbc.JdbcProfile
//
///**
//  * Created by johann on 13/04/17.
//  */
//trait DatabaseConfigComponent[Profile <: JdbcProfile] {
//
//  protected val dbConfig: DatabaseConfig[Profile]
//
//  protected final lazy val profile: Profile = dbConfig.profile
//
//  // Would be nice but is broken :/
////  protected final def db: Profile#Backend#Database = dbConfig.db
//    protected final def db: Profile#Backend#Database = profile.api.Database.forConfig("", config = dbConfig.config)
//
//}
