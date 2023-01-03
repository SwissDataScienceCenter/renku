package ch.renku.acceptancetests.tooling

import io.circe.Decoder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import io.circe.literal._

class GraphStatusSpec extends AnyFlatSpec with should.Matchers {

  it should "decode legacy structures" in {
    val legacyStatus =
      json"""
        { "done": 1, "total": 4, "progress": 0.25 }
          """

    Decoder[GraphStatus].decodeJson(legacyStatus) shouldBe Right(GraphStatus.Legacy(1, 4, 0.25))
  }

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
      GraphStatus.Status(
        true,
        GraphStatus.Progress(1, 4, 0.25f),
        GraphStatus.Details("in-progress", "Thing is in progress.")
      )
    )
  }
}
