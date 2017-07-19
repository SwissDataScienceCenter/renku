package authorization

import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}
import java.time.Instant
import javax.inject.{Inject, Singleton}

import ch.datascience.service.security.{PrivateKeyReader, PublicKeyReader}
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import play.api.Configuration

/**
  * Created by johann on 17/07/17.
  */
@Singleton
class TokenSignerProvider @Inject() (configuration: Configuration) {

  def get: Algorithm = algorithm

  def addDefaultHeadersAndClaims(builder: JWTCreator.Builder): JWTCreator.Builder = {
    builder.withIssuer("resource-manager").withIssuedAt(java.util.Date.from(Instant.now()))
    // TODO: expires at
  }

  private[this] lazy val algorithm: Algorithm = {
    val publicKey: RSAPublicKey = PublicKeyReader.readRSAPublicKey(configuration.getString("resource-manager.key.public-key").get)
    val privateKey: RSAPrivateKey = PrivateKeyReader.readRSAPrivateKey(configuration.getString("resource-manager.key.private-key").get)
    Algorithm.RSA256(publicKey, privateKey)
  }

}
