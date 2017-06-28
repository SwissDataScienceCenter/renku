package ch.datascience.graph.elements.tinkerpop_mappers

/**
  * Created by johann on 30/05/17.
  */
case object VertexIdReader extends SyncReader[Any, Long] {

  def readSync(x: Any): Long = x.asInstanceOf[Long]

}
