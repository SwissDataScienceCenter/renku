package ch.datascience.graph.elements.persisted.json

import ch.datascience.graph.elements.json.KeyFormat
import ch.datascience.graph.elements.persisted._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 13/06/17.
  */
object PathMappers {

  object PathFormat extends Format[Path] {

    def writes(path: Path): JsValue = path match {
      case p: VertexPath => VertexPathFormat.writes(p)
      case p: EdgePath => EdgePathFormat.writes(p)
      case p: PropertyPathFromRecord => PropertyPathFromRecordFormat.writes(p)
      case p: VertexPropertyPath => VertexPropertyFormat.writes(p)

      case _ => unsupportedPathFormat.writes(path)
    }

    def reads(json: JsValue): JsResult[Path] = reader.reads(json)


    private[this] lazy val reader: Reads[Path] = (JsPath \ "type").read[String].flatMap {
      case "vertex" => VertexPathFormat.map { path => path: Path }
      case "edge" => EdgePathFormat.map { path => path: Path }
      case "property" => PropertyPathFromRecordFormat.map { path => path: Path }
      case "vertex_property" => VertexPropertyFormat.map { path => path: Path }

      case t => unsupportedPathFormat
    }

    private[this] lazy val unsupportedPathFormat: Format[Path] = new Format[Path] {

      def writes(path: Path): JsValue = throw new IllegalArgumentException(s"Unsupported path: $path")

      def reads(json: JsValue): JsResult[Path] = JsError(s"Unsupported path type: ${(json \ "type").as[String]}")

    }

  }

  object VertexPathFormat extends Format[VertexPath] {

    def writes(path: VertexPath): JsValue = writer.writes(path)

    def reads(json: JsValue): JsResult[VertexPath] = reader.reads(json)

    private[this] lazy val writer: Writes[VertexPath] = (
      (JsPath \ "type").write[String] and
        (JsPath \ "id").write[PersistedVertex#Id]
      ) { path => ("vertex", path.vertexId) }

    private[this] lazy val reader: Reads[VertexPath] = (
      (JsPath \ "type").read[String].filter(typeError)(_ == "vertex") and
        (JsPath \ "id").read[PersistedVertex#Id]
      ) { (_, vertexId) => VertexPath(vertexId) }

    private[this] lazy val typeError = ValidationError("expected type: 'vertex'")

  }

  object EdgePathFormat extends Format[EdgePath] {

    def writes(path: EdgePath): JsValue = writer.writes(path)

    def reads(json: JsValue): JsResult[EdgePath] = reader.reads(json)

    private[this] lazy val writer: Writes[EdgePath] = (
      (JsPath \ "type").write[String] and
        (JsPath \ "id").write[PersistedEdge#Id]
      ) { path => ("edge", path.edgeId) }

    private[this] lazy val reader: Reads[EdgePath] = (
      (JsPath \ "type").read[String].filter(typeError)(_ == "edge") and
        (JsPath \ "id").read[PersistedEdge#Id]
      ) { (_, edgeId) => EdgePath(edgeId) }

    private[this] lazy val typeError = ValidationError("expected type: 'edge'")

  }

  object PropertyPathFromRecordFormat extends Format[PropertyPathFromRecord] {

    def writes(path: PropertyPathFromRecord): JsValue = writer.writes(path)

    def reads(json: JsValue): JsResult[PropertyPathFromRecord] = reader.reads(json)

    private[this] lazy val writer: Writes[PropertyPathFromRecord] = (
      (JsPath \ "type").write[String] and
        (JsPath \ "parent").write[Path](PathFormat) and
        (JsPath \ "key").write[PersistedProperty#Key](KeyFormat)
      ) { path => ("property", path.parent, path.key) }

    private[this] lazy val reader: Reads[PropertyPathFromRecord] = (
      (JsPath \ "type").read[String].filter(typeError)(_ == "property") and
        (JsPath \ "parent").read[Path](PathFormat) and
        (JsPath \ "key").read[PersistedProperty#Key](KeyFormat)
      ) { (_, parent, key) => PropertyPathFromRecord(parent, key) }

    private[this] lazy val typeError = ValidationError("expected type: 'property'")

  }

  object VertexPropertyFormat extends Format[VertexPropertyPath] {

    def writes(path: VertexPropertyPath): JsValue = writer.writes(path)

    def reads(json: JsValue): JsResult[VertexPropertyPath] = reader.reads(json)

    private[this] lazy val writer: Writes[VertexPropertyPath] = (
      (JsPath \ "type").write[String] and
        (JsPath \ "parent").write[Path](PathFormat) and
        (JsPath \ "id").write[PersistedVertexProperty#Id]
      ) { path => ("vertex_property", path.parent, path.propertyId) }

    private[this] lazy val reader: Reads[VertexPropertyPath] = (
      (JsPath \ "type").read[String].filter(typeError)(_ == "vertex_property") and
        (JsPath \ "parent").read[Path](PathFormat) and
        (JsPath \ "id").read[PersistedVertexProperty#Id]
      ) { (_, parent, propertyId) => VertexPropertyPath(parent, propertyId) }

    private[this] lazy val typeError = ValidationError("expected type: 'vertex_property'")

  }

}
