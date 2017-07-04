package models

/**
  * Created by jeberle on 09.06.17.
  */
case class WriteResourceRequest(appId: Option[Long], bucket: Long ,target: Either[String, Long])
