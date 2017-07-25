package models

/**
  * Created by johann on 10/07/17.
  */
trait Deployment {

  /**
    * @return deployment id, from knowledge graph
    */
  def id: Long

  def backend: String

  /**
    * @return deployment id, from the computing environment backend
    */
  def backendId: String

  def parentId: Option[Long]

  def environment: Map[String, String]

  def started: Boolean

  def completed: Boolean

}
