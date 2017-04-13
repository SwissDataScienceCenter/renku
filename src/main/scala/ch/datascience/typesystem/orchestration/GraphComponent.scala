package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.graphdb.{GraphStack, ManagementActionRunner}

/**
  * Created by johann on 04/04/17.
  */
trait GraphComponent {

  val gdb: ManagementActionRunner

  val gal: GraphStack

}
