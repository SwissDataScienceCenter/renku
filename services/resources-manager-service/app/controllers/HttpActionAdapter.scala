package controllers

import org.pac4j.core.context.HttpConstants
import org.pac4j.play.PlayWebContext
import org.pac4j.play.http.DefaultHttpActionAdapter
import play.mvc.Results
import play.mvc.Result

class HttpActionAdapter extends DefaultHttpActionAdapter {

  override def adapt(code: Int, context: PlayWebContext): Result = {
    if (code == HttpConstants.UNAUTHORIZED) {
      Results.unauthorized("401").as(HttpConstants.HTML_CONTENT_TYPE)
    } else if (code == HttpConstants.FORBIDDEN) {
      Results.forbidden("403").as(HttpConstants.HTML_CONTENT_TYPE)
    } else {
      super.adapt(code, context)
    }
  }
}