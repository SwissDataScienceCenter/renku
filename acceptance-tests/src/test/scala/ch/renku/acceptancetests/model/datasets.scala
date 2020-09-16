package ch.renku.acceptancetests.model
import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty

object datasets {

  final case class DatasetName(value: String Refined NonEmpty) {
    override lazy val toString: String = value.value
  }

  object DatasetName {
    def generate: DatasetName =
      DatasetName(nonEmptyStrings().generateOne)
  }

}
