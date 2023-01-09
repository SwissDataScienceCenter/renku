package ch.renku.acceptancetests.tooling

import io.circe.Decoder

sealed trait GraphStatus {
  def progressPercentage: Double
  def total:              Int

  final def widen: GraphStatus = this
}

object GraphStatus {

  final case class Legacy(done: Int, total: Int, progress: Double) extends GraphStatus {
    val progressPercentage: Double = progress
  }

  object Legacy {
    implicit val jsonDecoder: Decoder[Legacy] =
      Decoder.forProduct3("done", "total", "progress")(Legacy.apply)
  }

  final case class Progress(done: Int, total: Int, percentage: Float)
  object Progress {
    implicit val jsonDecoder: Decoder[Progress] =
      Decoder.forProduct3("done", "total", "percentage")(Progress.apply)
  }

  final case class Details(status: String, message: String)
  object Details {
    implicit val jsonDecoder: Decoder[Details] =
      Decoder.forProduct2("status", "message")(Details.apply)
  }

  final case class Status(activated: Boolean, progress: Progress, details: Details) extends GraphStatus {
    val progressPercentage: Double = progress.percentage.toDouble
    val total:              Int    = progress.total
  }

  object Status {
    implicit val jsonDecoder: Decoder[Status] =
      Decoder.forProduct3("activated", "progress", "details")(Status.apply)
  }

  implicit val jsonDecoder: Decoder[GraphStatus] =
    Status.jsonDecoder.map(_.widen).or(Legacy.jsonDecoder.map(_.widen))
}
