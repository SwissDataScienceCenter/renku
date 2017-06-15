package ch.datascience.graph.elements.tinkerpop_mappers

/**
  * Created by johann on 30/05/17.
  */
case object EdgeIdReader extends SyncReader[Any, String] {

  def readSync(x: Any): String = x.toString

}
