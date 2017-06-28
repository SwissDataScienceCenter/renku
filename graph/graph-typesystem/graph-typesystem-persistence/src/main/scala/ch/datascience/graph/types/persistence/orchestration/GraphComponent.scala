package ch.datascience.graph.types.persistence.orchestration

import ch.datascience.graph.types.persistence.graphdb.{GraphStack, ManagementActionRunner}

/**
  * Created by johann on 04/04/17.
  */
trait GraphComponent {

  protected def gdb: ManagementActionRunner

  protected def gal: GraphStack

}
