package authorization

import java.security.interfaces.RSAPublicKey
import javax.inject.{Inject, Singleton}

import ch.datascience.service.security.PublicKeyReader
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.{JWT, JWTVerifier}
import play.api.Configuration

/**
  * Created by johann on 14/07/17.
  */
@Singleton
class JWTVerifierProvider @Inject() (configuration: Configuration) {

  def get: JWTVerifier = verifier

  private[this] lazy val verifier: JWTVerifier = {
    val publicKey: RSAPublicKey = PublicKeyReader.readRSAPublicKey(configuration.getString("key.keycloak.public").get)
    val algorithm = Algorithm.RSA256(publicKey, null)
    JWT.require(algorithm).build()
  }

}
