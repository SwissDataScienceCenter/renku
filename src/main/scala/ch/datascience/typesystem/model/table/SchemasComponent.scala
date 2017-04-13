package ch.datascience.typesystem.model.table

import ch.datascience.typesystem.external.DatabaseConfigComponent
import slick.basic.BasicProfile
import scala.collection.mutable

/**
  * Created by johann on 13/04/17.
  */
trait SchemasComponent { this : JdbcProfileComponent =>

  import profile.api._

  final type Schema = profile.SchemaDescription

  protected final lazy val _schemas: mutable.Builder[Schema, Seq[Schema]] = Seq.newBuilder[Schema]
  final def schemas: Seq[Schema] = _schemas.result()

}
