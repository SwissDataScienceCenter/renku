package ch.renku.acceptancetests.tooling

import java.lang.System.getProperty

/**
  * Configuration specific to the BatchRemove spec.
  */
trait BatchRemoveProjectSpecData {

  case class BatchRemoveConfig(batchRemove: Boolean = false,
                               pattern:     String  = "test-(\\d{4})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})")

  protected lazy val batchRemoveConfig: Option[BatchRemoveConfig] = {

    val batchRemove = Option(getProperty("batchRem")) orElse sys.env.get("RENKU_TEST_BATCH_REMOVE") match {
      case Some(s) => Some(s.toBoolean)
      case None    => None
    }
    val projectNamePattern = Option(getProperty("remPattern")) orElse sys.env.get("RENKU_TEST_REMOVE_PATTERN");

    batchRemove match {
      case Some(b) => {
        projectNamePattern match {
          case Some(p) => Some(BatchRemoveConfig(b, p))
          case None    => Some(BatchRemoveConfig(b))
        }
      }
      case None => None
    }
  }
}
