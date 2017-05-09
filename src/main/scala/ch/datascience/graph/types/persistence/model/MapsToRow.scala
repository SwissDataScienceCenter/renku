package ch.datascience.graph.types.persistence.model

import ch.datascience.graph.types.persistence.model.relational.RowType

/**
  * Created by johann on 09/05/17.
  */
trait MapsToRow[+Row <: RowType] {

  def toRow: Row

}
