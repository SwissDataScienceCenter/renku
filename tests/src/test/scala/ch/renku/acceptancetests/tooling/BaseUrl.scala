package ch.renku.acceptancetests.tooling

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

abstract class BaseUrl(value: String Refined Url) {
  override lazy val toString: String = value.toString()
}
