package models

/**
  * Created by jeberle on 09.06.17.
  */
case class JobStatus(startTime: Option[String], completionTime: Option[String], succeeded: Option[Int], failed: Option[Int], jobs: List[PodStatus])

