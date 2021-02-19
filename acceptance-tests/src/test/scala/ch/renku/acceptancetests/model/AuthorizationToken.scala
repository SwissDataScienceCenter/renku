package ch.renku.acceptancetests.model

import org.http4s.AuthScheme.Bearer
import org.http4s.Credentials.Token
import org.http4s.Header
import org.http4s.headers.Authorization

sealed trait AuthorizationToken {
  val value: String
  def asHeader: Header
}

object AuthorizationToken {
  final case class OAuthAccessToken(value: String) extends AuthorizationToken {
    override lazy val asHeader: Header = Authorization(Token(Bearer, value))
  }
}
