package ch.datascience.graph.execution

import org.apache.tinkerpop.gremlin.structure.Graph

/**
  * Created by johann on 13/06/17.
  */
trait GraphExecutionContext {

  protected def graph: Graph

  def execute[A](body: => A): A = {
    // Get a clean transaction
    val tx = graph.tx()
    tx.rollback()

    // Execute body and commit
    try {
      val res = body
      tx.commit()
      res
    } catch {
      case e: Throwable =>
        tx.rollback()
        throw e
    }
  }

}
