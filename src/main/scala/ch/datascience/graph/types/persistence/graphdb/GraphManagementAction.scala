package ch.datascience.graph.types.persistence.graphdb

import org.janusgraph.core.schema.JanusGraphManagement

/**
  * Created by johann on 22/03/17.
  */
trait GraphManagementAction[+A] extends (JanusGraphManagement => A) {

  def map[B](f: (A) => B): GraphManagementAction[B]

  def flatMap[B](f: (A) => GraphManagementAction[B]): GraphManagementAction[B]

}

object GraphManagementAction {

  def apply[A](f: =>(JanusGraphManagement) => A): GraphManagementAction[A] = new GraphManagementActionImpl(f)

  private[this] class GraphManagementActionImpl[A](action: (JanusGraphManagement) => A) extends GraphManagementAction[A] {

    override def map[B](f: (A) => B): GraphManagementAction[B] = GraphManagementAction { this andThen f }

    override def flatMap[B](f: (A) => GraphManagementAction[B]): GraphManagementAction[B] = GraphManagementAction { mgmt: JanusGraphManagement =>
      val actionB: GraphManagementAction[B] = f(this(mgmt))
      actionB(mgmt)
    }

    override def apply(mgmt: JanusGraphManagement): A = action(mgmt)

  }

}
