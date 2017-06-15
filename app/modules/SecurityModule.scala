package modules

import com.google.inject.AbstractModule
import controllers.{ResourcesManagerAuthorizer, HttpActionAdapter}
import org.pac4j.core.client.Clients
import org.pac4j.oidc.client.OidcClient
import play.api.{Configuration, Environment}
import org.pac4j.play.store.{PlayCacheSessionStore, PlaySessionStore}
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.config.Config
import org.pac4j.http.client.direct.ParameterClient
import org.pac4j.jwt.config.signature.{RSASignatureConfiguration, SecretSignatureConfiguration}
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import java.security.{KeyFactory, KeyPair}
import java.security.spec.{PKCS8EncodedKeySpec, RSAPublicKeySpec, X509EncodedKeySpec}
import java.util.Base64


class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {

    //to be used if providing a user interface with openid connect
    /*val oidcConfiguration = new OidcConfiguration()
    oidcConfiguration.setClientId("storage-service")
    oidcConfiguration.setSecret("80183a19-6e47-42ac-aaa9-683726de857e")
    oidcConfiguration.setDiscoveryURI("https://internal.datascience.ch:8089/auth/realms/SDSC/.well-known/openid-configuration")
    val oidcClient = new OidcClient[OidcProfile](oidcConfiguration)
    oidcClient.addAuthorizationGenerator(new RoleAdminAuthGenerator)
   */

    val user_jwtAuthenticator = new JwtAuthenticator()
    val user_key = Base64.getDecoder.decode(configuration.getString("publicKey").get)
    val user_spec = new X509EncodedKeySpec(user_key)
    val user_kf = KeyFactory.getInstance("RSA")
    val user_pair = new KeyPair(user_kf.generatePublic(user_spec), null)
    user_jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(user_pair))
    val user_parameterClient = new ParameterClient("user_token", user_jwtAuthenticator)
    user_parameterClient.setSupportGetRequest(true)
    user_parameterClient.setSupportPostRequest(true)

    //claback url only used for OIDC
    val clients = new Clients("http://localhost:9000/callback", user_parameterClient)

    val config = new Config(clients)
    config.addAuthorizer("resources_manager", new ResourcesManagerAuthorizer())
    config.setHttpActionAdapter(new HttpActionAdapter())
    bind(classOf[Config]).toInstance(config)

    bind(classOf[PlaySessionStore]).to(classOf[PlayCacheSessionStore])

  }
}