package models

/**
  * Created by jeberle on 09.06.17.
  */
case class PodStatus(name: String, phase: String, reason: Option[String], message: Option[String])

