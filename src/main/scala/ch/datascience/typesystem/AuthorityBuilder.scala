package ch.datascience.typesystem

/**
  * Created by johann on 07/03/17.
  */
class AuthorityBuilder(private val _name: Option[String]) {

  def this() {
    this(None)
  }

  def name(name: String): AuthorityBuilder = {
    new AuthorityBuilder(Some(name))
  }

  def make(): Authority = _name match {
    case None => throw new IllegalArgumentException("authority name must be provided")
    case Some(name) => AuthorityImpl(name)
  }

  case class AuthorityImpl(name: String) extends Authority

}
