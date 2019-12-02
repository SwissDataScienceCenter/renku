package ch.renku.acceptancetests.tooling

import org.scalatest.concurrent.{AbstractPatienceConfiguration, PatienceConfiguration}
import org.scalatest.time.{Millis, Seconds, Span}

trait AcceptanceSpecPatience extends AbstractPatienceConfiguration { this: PatienceConfiguration =>

  implicit abstract override val patienceConfig: PatienceConfig = PatienceConfig(
    timeout  = scaled(Span(30, Seconds)),
    interval = scaled(Span(150, Millis))
  )
}
