/*
 * Copyright 2022 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.model.users.UserCredentials

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter}
import java.nio.file.Files.createDirectory
import java.nio.file.{Path, Paths}
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter
import java.util.concurrent.LinkedBlockingQueue
import scala.sys.process.{BasicIO, Process}

package object console {

  /** Execute a command, throws an exception if the command fails
    */
  def %>(command: Command)(implicit workPath: Path, userCredentials: UserCredentials): Unit =
    new CommandExecutor(command).execute

  def %>&>(command: Command)(implicit workPath: Path, userCredentials: UserCredentials): DetachedCommand = {
    val detachedCommand = new DetachedCommand()
    val process         = Process(command.toString)
    process run BasicIO.standard(inputFn(detachedCommand)(_))
    detachedCommand
  }

  /** Execute a command and return a String either stdout or stderr and does not throw an exception
    */
  def %%>(command: Command)(implicit workPath: Path, userCredentials: UserCredentials): String =
    new CommandExecutor(command).safeExecute

  val rootWorkDirectory: Path = Paths.get("target")

  def createTempFolder: Path = {
    val timestampPattern = DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmm_ss")
    val folder           = rootWorkDirectory.toUri resolve (now format timestampPattern)
    createDirectory(Paths.get(folder))
  }

  implicit class CommandOps(val context: StringContext) {

    def c(args: Any*): Command = Command {
      context.parts.zipAll(args, "", "").foldLeft("") { case (command, (part, arg)) =>
        s"$command$part$arg"
      }
    }
  }

  implicit class PathOps(path: Path) {
    def /(otherPath: Path) = Paths.get(path.toString, otherPath.toString)

    def /(otherPath: String) = Paths.get(path.toString, otherPath)
  }

  final class DetachedCommand() {
    val inputString = new LinkedBlockingQueue[String](1)

    def write(content: String): Unit =
      inputString.put(content)
  }

  private def inputFn(detachedCommand: DetachedCommand)(stdIn: OutputStream): Unit = {
    val writer = new BufferedWriter(new OutputStreamWriter(stdIn))
    writer.write(detachedCommand.inputString.take() + "\n\n")
    writer.flush()
    stdIn.close()
  }
}
