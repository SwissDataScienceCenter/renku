package ch.renku.acceptancetests.tooling

case class TestsDefaults(env:      Option[String],
                         email:    Option[String] = None,
                         username: Option[String] = None,
                         password: Option[String] = None,
                         fullname: Option[String] = None)

object TestsDefaults {
  import java.nio.file.Paths

  import pureconfig.ConfigSource
  import pureconfig.generic.auto._

  private val defaultConfigFileName = "./tests-defaults.conf"

  def apply(): TestsDefaults =
    if (Paths.get(defaultConfigFileName).toFile.exists())
      ConfigSource
        .file(defaultConfigFileName)
        .load[TestsDefaults]
        .getOrElse(TestsDefaults(Some("https://dev.renku.ch")))
    else
      TestsDefaults(Some("https://dev.renku.ch"))
}
