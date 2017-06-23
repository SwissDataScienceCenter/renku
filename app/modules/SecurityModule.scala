package modules

import com.google.inject.AbstractModule
import controllers.{ApiManagerAuthorizer, HttpActionAdapter}
import org.pac4j.core.client.Clients
import play.api.{Configuration, Environment}
import org.pac4j.play.store.{PlayCacheSessionStore, PlaySessionStore}
import org.pac4j.core.config.Config
import org.pac4j.http.client.direct.{HeaderClient, ParameterClient}
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import java.security.{KeyFactory, KeyPair}
import java.security.spec.X509EncodedKeySpec
import java.util.Base64


class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {

    val user_jwtAuthenticator = new JwtAuthenticator()
    val user_key = Base64.getDecoder.decode(configuration.getString("key.keycloak.public").get)
    val user_spec = new X509EncodedKeySpec(user_key)
    val user_kf = KeyFactory.getInstance("RSA")
    val user_pair = new KeyPair(user_kf.generatePublic(user_spec), null)
    user_jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(user_pair))
    val user_parameterClient = new HeaderClient("Authorization", "Bearer ", user_jwtAuthenticator)


    val jwtAuthenticator = new JwtAuthenticator()
    val key = Base64.getDecoder.decode(configuration.getString("key.keycloak.public").get)
    val spec = new X509EncodedKeySpec(key)
    val kf = KeyFactory.getInstance("RSA")
    val pair = new KeyPair(kf.generatePublic(spec), null)
    jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(pair))
    val parameterClient = new ParameterClient("token", jwtAuthenticator)
    parameterClient.setSupportGetRequest(true)
    parameterClient.setSupportPostRequest(true)


    val clients = new Clients(user_parameterClient, parameterClient)

    val config = new Config(clients)
    config.addAuthorizer("api_manager", new ApiManagerAuthorizer())
    config.setHttpActionAdapter(new HttpActionAdapter())
    bind(classOf[Config]).toInstance(config)

    bind(classOf[PlaySessionStore]).to(classOf[PlayCacheSessionStore])

  }
}
