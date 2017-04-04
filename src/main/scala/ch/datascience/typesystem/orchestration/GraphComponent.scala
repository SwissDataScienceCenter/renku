package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.graphdb.GraphAccessLayer

/**
  * Created by johann on 04/04/17.
  */
trait GraphComponent {

  val gal: GraphAccessLayer

}
