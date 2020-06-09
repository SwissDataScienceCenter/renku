package ch.renku.acceptancetests.tooling

import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty

case class RenkuVersion(value: String Refined NonEmpty)
