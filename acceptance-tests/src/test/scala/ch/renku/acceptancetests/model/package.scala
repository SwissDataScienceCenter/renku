package ch.renku.acceptancetests

import ch.renku.acceptancetests.tooling.UrlEncoder._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

package object model {
  final case class RenkuBaseUrl(value: String Refined Url) extends BaseUrl with UrlOps[RenkuBaseUrl]
  final case class GitLabBaseUrl(value: String Refined Url) extends BaseUrl with UrlOps[GitLabBaseUrl]
  final case class GitLabApiUrl(baseUrl: GitLabBaseUrl) extends BaseUrl with UrlOps[GitLabApiUrl] {
    override val value: String Refined Url = Refined.unsafeApply(s"$baseUrl/api/v4")
  }

  abstract class BaseUrl {
    val value: String Refined Url
    override lazy val toString: String = value.toString()
  }

  trait UrlOps[T <: BaseUrl] {
    self: T =>

    def /(part: String): UrlNoQueryParam = UrlNoQueryParam.unsafe(s"$value/${urlEncode(part)}")

    def /(maybePart: Option[String]): UrlNoQueryParam = maybePart match {
      case Some(value) => UrlNoQueryParam.unsafe(s"$value/${urlEncode(value)}")
      case _           => UrlNoQueryParam(value)
    }

    def param(key: String, value: Any): UrlWithQueryParam =
      UrlWithQueryParam.unsafe(
        if (value.toString contains s"$key=")
          value.toString
            .replaceAll(s"(\\?$key=[\\w\\d%\\+]*)", s"?$key=${urlEncode(value.toString)}")
            .replaceAll(s"(&$key=[\\w\\d%\\+]*)", s"&$key=${urlEncode(value.toString)}")
        else if (value.toString contains "?")
          s"$value&$key=${urlEncode(value.toString)}"
        else
          s"$value?$key=${urlEncode(value.toString)}"
      )
  }

  final case class UrlNoQueryParam(value: String Refined Url) extends BaseUrl with UrlOps[UrlNoQueryParam]
  object UrlNoQueryParam {
    def unsafe(value: String): UrlNoQueryParam = UrlNoQueryParam(Refined.unsafeApply(value))
  }

  final case class UrlWithQueryParam(value: String Refined Url) extends BaseUrl with UrlOps[UrlWithQueryParam] {

    def and(key: String, value: Any): UrlWithQueryParam = add(key, value)

    def and(key: String, maybeValue: Option[Any]): UrlWithQueryParam =
      maybeValue match {
        case None        => this
        case Some(value) => add(key, value)
      }

    private def add(key: String, value: Any) = UrlWithQueryParam.unsafe {
      if (value.toString contains s"$key=")
        value.toString
          .replaceAll(s"(\\?$key=[\\w\\d%\\+]*)", s"?$key=${urlEncode(value.toString)}")
          .replaceAll(s"(&$key=[\\w\\d%\\+]*)", s"&$key=${urlEncode(value.toString)}")
      else
        s"$value  &$key=${urlEncode(value.toString)}"
    }
  }
  object UrlWithQueryParam {
    def unsafe(value: String): UrlWithQueryParam = UrlWithQueryParam(Refined.unsafeApply(value))
  }
}
