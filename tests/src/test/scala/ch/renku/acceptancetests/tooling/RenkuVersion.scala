package ch.renku.acceptancetests.tooling

final case class RenkuCliConfig(version: RenkuVersion, installCommand: RenkuInstallCommand)

final case class RenkuVersion(value: String) {
  assert(value.trim.nonEmpty, "'$value' not a valid renku version")
  override lazy val toString: String = value
}

final case class RenkuInstallCommand(value: String) {
  assert(value.trim.nonEmpty, "'$value' not a valid renku install command")
  override lazy val toString: String = value
}
