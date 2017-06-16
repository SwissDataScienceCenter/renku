package controllers
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.security.{KeyFactory, KeyPair}
import java.util.Base64
import javax.inject.{Inject, Singleton}

import org.pac4j.core.profile.{CommonProfile, ProfileManager}

import scala.collection.JavaConversions._
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import play.api.mvc.{Action, BodyParsers, Controller, RequestHeader}

import scala.concurrent.Future

/**
  * Created by jeberle on 25.04.17.
  */
@Singleton
class PermissionController @Inject()(config: play.api.Configuration, val playSessionStore: PlaySessionStore) extends Controller {

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  private def getGenerator = {
    val public_key = Base64.getDecoder.decode(config.getString("key.resource-manager.public").get)
    val private_key = Base64.getDecoder.decode(config.getString("key.resource-manager.private").get)
    val public_spec = new X509EncodedKeySpec(public_key)
    val private_spec = new PKCS8EncodedKeySpec(private_key)
    val kf = KeyFactory.getInstance("RSA")
    val key_pair = new KeyPair(kf.generatePublic(public_spec), kf.generatePrivate(private_spec))
    val signConfig = new RSASignatureConfiguration(key_pair)

    new JwtGenerator(signConfig)
  }

  def authorizeStorageRead(id: Long) = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getGenerator.generate(Map("sub" -> "StorageService", "user_id" -> profile.getId, "file_uuid" -> "uuid", "scope" -> "storage:read"))

      Ok("{\"permission_token\": \"" + token + "\"}")
    }
  }

  def authorizeStorageWrite = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getGenerator.generate(Map("sub" -> "StorageService", "user_id" -> profile.getId, "file_uuid" -> "uuid", "scope" -> "storage:write"))

      Ok("{\"permission_token\": \"" + token + "\"}")
    }
  }

  def authorizeComputeExecute = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getGenerator.generate(Map("sub" -> "DeployService", "user_id" -> profile.getId, "scope" -> "compute:execute"))

      Ok("{\"permission_token\": \"" + token + "\"}")
    }
  }

}
