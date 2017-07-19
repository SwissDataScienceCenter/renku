package authorization

import java.security.interfaces.RSAPublicKey
import javax.inject.{Inject, Singleton}

import ch.datascience.service.security.PublicKeyReader
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.{JWT, JWTVerifier}
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

/**
  * Created by johann on 14/07/17.
  */
@Singleton
class JWTVerifierProvider @Inject() (configuration: Configuration, wSClient: WSClient, executionContext: ExecutionContext) {

  def get: JWTVerifier = verifier

  private[this] lazy val verifier: JWTVerifier = {
    val publicKey: RSAPublicKey = getPublicKey
    val algorithm = Algorithm.RSA256(publicKey, null)
    JWT.require(algorithm).build()
  }

  private[this] def getPublicKey: RSAPublicKey = {
    implicit val ws: WSClient = wSClient
    implicit val ec: ExecutionContext = executionContext
    val futureKey = PublicKeyReader.getRSAPublicKey(configuration.getConfig("identity-manager.key").get)
    Await.result(futureKey, 2.minutes)
  }

}
