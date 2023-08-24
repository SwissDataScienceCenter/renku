/*
 * Copyright 2023 Swiss Data Science Center (SDSC)
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

import io.circe.Decoder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import io.circe.literal._

class GraphStatusSpec extends AnyFlatSpec with should.Matchers {

  it should "decode detailed status response" in {
    val detailedStatus =
      json"""
            { "activated": true,
              "progress": {
                "done": 1,
                "total": 4,
                "percentage": 0.25
              },
              "details": {
                "status": "in-progress",
                "message": "Thing is in progress."
              }
            }
          """

    Decoder[GraphStatus].decodeJson(detailedStatus) shouldBe Right(
      GraphStatus(
        activated = true,
        GraphStatus.Progress(1, 4, 0.25f),
        Some(GraphStatus.Details("in-progress", "Thing is in progress.", maybeStackTrace = None))
      )
    )
  }

  it should "decode status response without details" in {
    val detailedStatus =
      json"""
            { "activated": true,
              "progress": {
                "done": 1,
                "total": 4,
                "percentage": 0.25
              }
            }
          """

    Decoder[GraphStatus].decodeJson(detailedStatus) shouldBe Right(
      GraphStatus(
        activated = true,
        GraphStatus.Progress(1, 4, 0.25f),
        maybeDetails = None
      )
    )
  }
}
