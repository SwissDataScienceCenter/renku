package controllers

import java.util.concurrent.TimeUnit
import javax.inject._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import org.javaswift.joss.client.factory.{AccountConfig, AccountFactory}
import org.javaswift.joss.headers.`object`.range.{FirstPartRange, LastPartRange, MidPartRange}
import org.javaswift.joss.instructions.DownloadInstructions
import org.javaswift.joss.model.Account
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import play.api.libs.streams._
import play.api.mvc._

import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.matching.Regex


@Singleton
class SwiftController @Inject()(config: play.api.Configuration, val playSessionStore: PlaySessionStore) extends Controller {

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  val swiftConfig = new AccountConfig()
  swiftConfig.setUsername(config.getString("swift.username").get)
  swiftConfig.setPassword(config.getString("swift.password").get)
  swiftConfig.setAuthUrl(config.getString("swift.auth_url").get)
  swiftConfig.setTenantId(config.getString("swift.project").get)
  val swiftAccount: Account = new AccountFactory(swiftConfig).createAccount()

  val RangePattern: Regex = """bytes=(\d+)?-(\d+)?.*""".r

  def read_object(name: String) = Action.async { implicit request => Future {
    val profile = getProfiles(request).head
    val CHUNK_SIZE = 100
    val container = swiftAccount.getContainer(profile.getId)
    if (container.exists() && container.getObject(name).exists()) {
      val instructions = new DownloadInstructions()
      request.headers.get("Range").map {
        case RangePattern(null, to) => instructions.setRange(new FirstPartRange(to.toInt))
        case RangePattern(from, null) => instructions.setRange(new LastPartRange(from.toInt))
        case RangePattern(from, to) => instructions.setRange(new MidPartRange(from.toInt, to.toInt))
        case _ =>
      }
      val data = container.getObject(name).downloadObjectAsInputStream(instructions)
      val dataContent: Source[ByteString, _] = StreamConverters.fromInputStream(() => data, CHUNK_SIZE)

      Ok.chunked(dataContent)
    } else {
      NotFound("File not found")
    }
  }
  }

  def write_object(name: String) = Action(forward(name)) { request =>
    request.body
  }

  def forward(name: String): BodyParser[Result] = BodyParser { req =>
    Accumulator.source[ByteString].mapFuture { source =>
      Future {
        val profile = getProfiles(req).head
        implicit val system = ActorSystem("Sys")
        implicit val materializer = ActorMaterializer()
        val container = swiftAccount.getContainer(profile.getId)
        if (!container.exists()) container.create()
        val obj = container.getObject(name)
        val inputStream = source.runWith(
          StreamConverters.asInputStream(FiniteDuration(3, TimeUnit.SECONDS))
        )
        obj.uploadObject(inputStream)
        Right(Created(""))
      }
    }
  }
}