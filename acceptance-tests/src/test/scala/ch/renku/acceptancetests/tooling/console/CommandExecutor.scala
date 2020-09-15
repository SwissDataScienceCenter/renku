package ch.renku.acceptancetests.tooling.console

import java.io.{File, InputStream}
import java.nio.file.Path
import java.util
import java.util.concurrent.ConcurrentLinkedQueue

import cats.effect.IO
import cats.implicits._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.console.Command.UserInput

import scala.jdk.CollectionConverters._
import scala.language.postfixOps
import scala.sys.process._

private class CommandExecutor(command: Command) {

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

  private def executeCommand(implicit workPath: Path,
                             output:            util.Collection[String],
                             userCredentials:   UserCredentials): Unit =
    command.userInputs.foldLeft(buildProcess) { (process, userInput) =>
      process #< userInput.asStream
    } lazyLines ProcessLogger(logLine _) foreach logLine

  private def buildProcess(implicit workPath: Path) =
    command.maybeFileName.foldLeft(Process(command.toString.stripMargin, workPath.toFile)) { (process, fileName) =>
      process #>> new File(workPath.toUri resolve fileName.value)
    }

  private def logLine(
      line:          String
  )(implicit output: util.Collection[String], userCredentials: UserCredentials): Unit = line.trim match {
    case "" => ()
    case line =>
      val obfuscatedLine = line.replace(userCredentials.password.value, "###")
      output add obfuscatedLine
      logger debug obfuscatedLine
  }

  private def consoleException(implicit output: util.Collection[String]): PartialFunction[Throwable, IO[String]] = {
    case _ =>
      ConsoleException {
        s"$command failed with:\n${output.asString}"
      }.raiseError[IO, String]
  }

  private def outputAsString(implicit output: util.Collection[String]): PartialFunction[Throwable, String] = {
    case _ => output.asString
  }

  private implicit class OutputOps(output: util.Collection[String]) {
    lazy val asString: String = output.asScala.mkString("\n")
  }

  private implicit class UserInputOps(userInput: UserInput) {
    import java.nio.charset.StandardCharsets.UTF_8

    lazy val asStream: InputStream = new java.io.ByteArrayInputStream(
      userInput.value.getBytes(UTF_8.name)
    )
  }
}
