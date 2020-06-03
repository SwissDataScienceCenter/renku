package ch.renku.acceptancetests.tooling.console

import Command._

final case class Command(command: String, userInputs: List[UserInput] = Nil) {
  assert(command.trim.nonEmpty, "Console command cannot be blank")

  def userInput(userInput: UserInput): Command = Command(command, userInputs :+ userInput)

  override lazy val toString: String = command
}

object Command {
  import eu.timepit.refined.api.Refined
  import eu.timepit.refined.collection.NonEmpty

  type UserInput = String Refined NonEmpty
}
