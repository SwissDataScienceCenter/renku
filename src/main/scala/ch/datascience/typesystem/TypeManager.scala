package ch.datascience.typesystem

import scala.util.Try

/**
  * Created by johann on 07/03/17.
  */
trait TypeManager {

  def findDomain(namespace: String): Option[GraphDomain]

  def createDomain(domain: GraphDomain): Try[GraphDomain]

  def findAuthority(name: String): Option[Authority]

  def createAuthority(authority: Authority): Try[Authority]

}
