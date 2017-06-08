package ch.datascience.graph.elements.mutation.worker

import scala.concurrent.Promise

/**
  * Created by johann on 07/06/17.
  */
class Queue[A] {

  def nonEmpty: Boolean = synchronized {
    self.nonEmpty
  }

  def enqueue(elems: A*): Unit = synchronized {
    self.enqueue(elems: _*)
    currentPromise match {
      case Some(p) =>
        p.success(())
        currentPromise = None
      case None => ()
    }
  }

  def dequeue: A = synchronized {
    self.dequeue()
  }

  def register(): Promise[Unit] = synchronized {
    currentPromise match {
      case Some(p) => p
      case None =>
        val p = Promise[Unit]()
        currentPromise = Some(p)
        p
    }
  }

  private[this] val self: scala.collection.mutable.Queue[A] = new scala.collection.mutable.Queue[A]

  private[this] var currentPromise: Option[Promise[Unit]] = None

}
