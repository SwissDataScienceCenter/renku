package ch.renku.acceptancetests.model

import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty

object users {

  final case class UserCredentials(
      email:       String Refined NonEmpty,
      username:    String Refined NonEmpty,
      password:    String Refined NonEmpty,
      fullName:    String Refined NonEmpty,
      useProvider: Boolean,
      register:    Boolean
  ) {

    /**
      * Return the user namespace given the user credentials.
      */
    def userNamespace: String = {
      val un = username.value
      un.replace("+", "")
    }
  }
}
