package ch.renku.acceptancetests.tooling

import ch.renku.acceptancetests.model.AuthorizationToken.OAuthAccessToken
import org.http4s.Status.Ok
import org.http4s.UrlForm
import org.scalatest.Matchers.fail

trait GitLab extends RestClient {
  self: AcceptanceSpecData =>

  lazy val oauthAccessToken: OAuthAccessToken =
    POST(gitLabBaseUrl / "oauth" / "token")
      .withEntity(
        UrlForm(
          "grant_type" -> "password",
          "username"   -> userCredentials.username.toString(),
          "password"   -> userCredentials.password.toString()
        )
      )
      .send
      .whenReceived(status = Ok)
      .bodyAsJson
      .extract(jsonRoot.`access_token`.string.getOption)
      .map(OAuthAccessToken.apply)
      .getOrElse(fail("OAuth Access Token couldn't be obtained"))
}
