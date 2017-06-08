//package models
//
//import javax.inject.{Inject,Singleton}
//import java.sql.Timestamp
//import java.util.UUID
//import slick.jdbc.JdbcProfile
//import slick.lifted.ProvenShape
//import slick.sql.SqlProfile.ColumnOption.SqlType
//import play.api.db.slick.DatabaseConfigProvider
//import play.db.NamedDatabase
//import scala.concurrent.Future
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
//import play.api.libs.json.JsValue
//
//case class Event(uuid : UUID, event : String, created : Timestamp)
//
//@Singleton
//class EventRepo @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) {
//
//    val dbConfig = dbConfigProvider.get[JdbcProfile]
//    val db = dbConfig.db
//    import dbConfig.profile.api._
//
//    class Events(tag : Tag) extends Table[Event](tag, "EVENT_LOG") {
//        def uuid    : Rep[UUID] = column[UUID]("EVENT_ID", O.PrimaryKey)
//        def event   : Rep[String] = column[String]("EVENT")
//        def created : Rep[Timestamp] = column[Timestamp]("CREATED")
//        def * : ProvenShape[Event] = (uuid, event, created) <> (Event.tupled, Event.unapply)
//    }
//
//    object events extends TableQuery(new Events(_)) {
//    }
//
//    def insert(uuid : UUID, event : JsValue) : Future[Event] = db.run {
//        (events.map(e => (e.uuid, e.event))
//           returning events.map(_.created)
//           into ((tuple, created) => Event(tuple._1, tuple._2, created))
//        ) += (uuid, event.toString())
//        //events += event).map(_ => ()
//    }
//
//    def insert(event : JsValue) : Future[Event] = insert(UUID.randomUUID(), event)
//}
