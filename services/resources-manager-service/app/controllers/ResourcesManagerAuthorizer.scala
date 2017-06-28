package controllers

import org.pac4j.core.authorization.authorizer.ProfileAuthorizer
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.CommonProfile

class ResourcesManagerAuthorizer extends ProfileAuthorizer[CommonProfile] {

  def isAuthorized(context: WebContext, profiles: java.util.List[CommonProfile]): Boolean = {
    return isAllAuthorized(context, profiles)
  }

  def isProfileAuthorized(context: WebContext, profile: CommonProfile): Boolean = {
    if (profile == null) {
      false
    } else {
        true  // accepts any user
    }
  }
}