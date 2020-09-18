package ch.renku.acceptancetests.tooling

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

object UrlEncoder {
  val urlEncode: String => String = URLEncoder.encode(_, UTF_8.name())
}
