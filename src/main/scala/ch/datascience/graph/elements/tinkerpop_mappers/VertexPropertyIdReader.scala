package ch.datascience.graph.elements.tinkerpop_mappers

/**
  * Created by johann on 30/05/17.
  */
case object VertexPropertyIdReader extends SyncReader[Any, String] {

  def readSync(id: Any): String = id.toString

}
