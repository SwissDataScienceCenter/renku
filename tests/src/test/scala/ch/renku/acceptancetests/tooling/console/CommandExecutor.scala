package ch.renku.acceptancetests.tooling.console

import java.io.{File, InputStream}
import java.nio.file.Path
import java.util
import java.util.concurrent.ConcurrentLinkedQueue

import cats.effect.IO
import cats.implicits._
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.console.Command.UserInput

import scala.jdk.CollectionConverters._
import scala.language.postfixOps
import scala.sys.process._

private class CommandExecutor(command: Command) {

  def execute(implicit workPath: Path): Unit = {

    implicit val output: util.Collection[String] = new ConcurrentLinkedQueue[String]()

    IO {
      command.userInputs.foldLeft(buildProcess) { (process, userInput) =>
        process #< userInput.asStream
      } lazyLines ProcessLogger(logLine _) foreach logLine
    } recoverWith consoleException
  }.unsafeRunSync()

  private def buildProcess(implicit workPath: Path) =
    command.maybeFileName.foldLeft(Process(command.toString.stripMargin, workPath.toFile)) { (process, fileName) =>
      process #>> new File(workPath.toUri resolve fileName.value)
    }

  private def logLine(line: String)(implicit output: util.Collection[String]): Unit = line.trim match {
    case "" => ()
    case line =>
      output add line
      logger debug line
  }

  private def consoleException(implicit output: util.Collection[String]): PartialFunction[Throwable, IO[Unit]] = {
    case _ =>
      ConsoleException {
        s"$command failed with:\n${output.asScala.mkString("\n")}"
      }.raiseError[IO, Unit]
  }

  private implicit class UserInputOps(userInput: UserInput) {
    import java.nio.charset.StandardCharsets.UTF_8

    lazy val asStream: InputStream = new java.io.ByteArrayInputStream(
      userInput.value.getBytes(UTF_8.name)
    )
  }
}
