package ch.datascience.graph.elements.tinkerpop_mappers

import java.util.UUID

import ch.datascience.graph.Constants
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.DataType
import ch.datascience.graph.values.BoxedValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 19/05/17.
  */
case class BoxedReader(scope: PropertyScope) extends KeyValueReader[Constants.Key, BoxedValue] {

  def read(x: (Constants.Key, Any))(implicit ec: ExecutionContext): Future[BoxedValue] = {
    for {
      definition <- scope.getPropertyFor(x._1)
    } yield definition.get.dataType match {
      case DataType.String => BoxedValue(x._2.asInstanceOf[String])
      case DataType.Character => BoxedValue(x._2.asInstanceOf[Char])
      case DataType.Boolean => BoxedValue(x._2.asInstanceOf[Boolean])
      case DataType.Byte => BoxedValue(x._2.asInstanceOf[Byte])
      case DataType.Short => BoxedValue(x._2.asInstanceOf[Short])
      case DataType.Integer => BoxedValue(x._2.asInstanceOf[Int])
      case DataType.Long => BoxedValue(x._2.asInstanceOf[Long])
      case DataType.Float => BoxedValue(x._2.asInstanceOf[Float])
      case DataType.Double => BoxedValue(x._2.asInstanceOf[Double])
      case DataType.UUID => BoxedValue(x._2.asInstanceOf[UUID])
    }
  }

}
