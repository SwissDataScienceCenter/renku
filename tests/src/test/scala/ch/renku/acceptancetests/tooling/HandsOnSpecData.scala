package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

/**
  * Configuration specific to the HandsOn spec.
  */
trait HandsOnSpecData {

  protected implicit lazy val captureScreenshots: Boolean = {
    Option(getProperty("docsrun")) orElse sys.env.get("RENKU_TEST_DOCS_RUN") match {
      case Some(s) => s.nonEmpty
      case None    => false
    }
  }
}
