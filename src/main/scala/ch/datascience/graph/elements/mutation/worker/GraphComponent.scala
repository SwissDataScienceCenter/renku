package ch.datascience.graph.elements.mutation.worker

import org.janusgraph.core.JanusGraph

/**
  * Created by johann on 08/06/17.
  */
trait GraphComponent {

  protected def graph: JanusGraph

  def execute[A](body: => A): A = {
    // Get a clean transaction
    val tx = graph.tx()
    tx.rollback()

    // Execute f
    try {
      val res = body
      tx.commit()
      res
    } catch {
      case e: Throwable =>
        println("Rolling back...")
        tx.rollback()
        throw e
    }
  }

}
