package ch.renku.acceptancetests.tooling

import java.nio.file.Files.createDirectory
import java.nio.file.{Path, Paths}
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter

package object console {

  /**
    * Execute a command, throws an exception if the command fails
    */
  def %>(command: Command)(implicit workPath: Path): Unit =
    new CommandExecutor(command).execute

  /**
    * Execute a command and return a String either stdout or stderr and does not throw an exception
    */
  def %%>(command: Command)(implicit workPath: Path): String =
    new CommandExecutor(command).safeExecute

  def createTempFolder: Path = {
    val workDirectory    = Paths.get("target")
    val timestampPattern = DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmm_ss")
    val folder           = workDirectory.toUri resolve (now format timestampPattern)
    createDirectory(Paths.get(folder))
  }

  implicit class CommandOps(val context: StringContext) {

    def c(args: Any*): Command = Command {
      context.parts.zipAll(args, "", "").foldLeft("") {
        case (command, (part, arg)) => s"$command$part$arg"
      }
    }
  }
}
