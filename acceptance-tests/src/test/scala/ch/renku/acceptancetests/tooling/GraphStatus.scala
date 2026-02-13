/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.tooling

import GraphStatus._
import io.circe.Decoder

final case class GraphStatus(activated: Boolean, progress: Progress, maybeDetails: Option[Details]) {
  val progressPercentage: Double = progress.percentage.toDouble
  val total:              Int    = progress.total
}

object GraphStatus {

  final case class Progress(done: Int, total: Int, percentage: Float)
  object Progress {
    implicit val jsonDecoder: Decoder[Progress] =
      Decoder.forProduct3("done", "total", "percentage")(Progress.apply)
  }

  final case class Details(status: String, message: String, maybeStackTrace: Option[String])
  object Details {
    implicit val jsonDecoder: Decoder[Details] =
      Decoder.forProduct3("status", "message", "stacktrace")(Details.apply)
  }

  implicit val jsonDecoder: Decoder[GraphStatus] =
    Decoder.forProduct3("activated", "progress", "details")(GraphStatus.apply)
}
