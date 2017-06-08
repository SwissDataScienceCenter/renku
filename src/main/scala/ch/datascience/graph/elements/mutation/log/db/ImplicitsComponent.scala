package ch.datascience.graph.elements.mutation.log.db

import java.time.Instant
import java.sql.Timestamp
import play.api.libs.json.{JsValue, Json}

/**
  * Created by johann on 07/06/17.
  */
trait ImplicitsComponent { this: JdbcProfileComponent =>

  import profile.api._

  implicit val jsonColumnType: BaseColumnType[JsValue] =
    MappedColumnType.base[JsValue, String](_.toString(), Json.parse)

  implicit val customTimestampColumnType: BaseColumnType[Instant] =
    MappedColumnType.base[Instant, Long](_.toEpochMilli, Instant.ofEpochMilli)

}
