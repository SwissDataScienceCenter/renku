package ch.renku.acceptancetests.tooling

final case class TestsDefaults(env:                 Option[String],
                               email:               Option[String] = None,
                               username:            Option[String] = None,
                               password:            Option[String] = None,
                               fullname:            Option[String] = None,
                               renkuVersion:        Option[String] = None,
                               renkuInstallCommand: String)

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
        .fold(error => throw new Exception(error.prettyPrint()), identity)
    else
      TestsDefaults(env                 = Some("https://dev.renku.ch"),
                    renkuInstallCommand = "python3 -m pip install 'renku==%s' --user")
}
