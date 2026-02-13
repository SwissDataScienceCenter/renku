/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
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

package ch.renku.acceptancetests.tooling.console

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.implicits._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.console.Command.UserInput

import java.io._
import java.nio.file.Path
import java.util
import java.util.concurrent.ConcurrentLinkedQueue
import scala.jdk.CollectionConverters._
import scala.sys.process._

private class CommandExecutor(command: Command)(implicit ioRuntime: IORuntime) {

  def execute(implicit workPath: Path, userCredentials: UserCredentials): String = {

    implicit val output: util.Collection[String] = new ConcurrentLinkedQueue[String]()

    IO {
      executeCommand
      output.asString
    } recoverWith consoleException
  }.unsafeRunSync()

  def safeExecute(implicit workPath: Path, userCredentials: UserCredentials): String = {
    implicit val output: util.Collection[String] = new ConcurrentLinkedQueue[String]()

    IO {
      executeCommand
      output.asString
    } recover outputAsString
  }.unsafeRunSync()

  private def executeCommand(implicit
      workPath:        Path,
      output:          util.Collection[String],
      userCredentials: UserCredentials
  ): Unit =
    command.userInputs.foldLeft(buildProcess) { (process, userInput) =>
      process #< userInput.asStream
    } lazyLines ProcessLogger(logLine _, logLine _) foreach logLine

  private def buildProcess(implicit workPath: Path) =
    command.maybeFileName.foldLeft(Process(command.toString.stripMargin, workPath.toFile)) { (process, fileName) =>
      process #>> new File(workPath.toUri resolve fileName)
    }

  private def logLine(
      line: String
  )(implicit output: util.Collection[String], userCredentials: UserCredentials): Unit = line.trim match {
    case "" => ()
    case line =>
      val obfuscatedLine = line.replace(userCredentials.password, "###")
      output add obfuscatedLine
      logger debug obfuscatedLine
  }

  private def consoleException(implicit output: util.Collection[String]): PartialFunction[Throwable, IO[String]] = {
    case _ =>
      ConsoleException {
        s"$command failed with:\n${output.asScala.mkString("\n")}"
      }.raiseError[IO, String]
  }

  private def outputAsString(implicit output: util.Collection[String]): PartialFunction[Throwable, String] = { case _ =>
    output.asString
  }

  private implicit class OutputOps(output: util.Collection[String]) {
    lazy val asString: String = output.asScala.mkString("\n")
  }

  private implicit class UserInputOps(userInput: UserInput) {
    import java.nio.charset.StandardCharsets.UTF_8

    lazy val asStream: InputStream = new java.io.ByteArrayInputStream(
      userInput.getBytes(UTF_8.name)
    )
  }
}
