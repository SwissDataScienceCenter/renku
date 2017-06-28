package controllers

import org.apache.commons.lang3.StringUtils
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.CommonProfile

class UserTokenAuthorizer extends ProfileAuthorizer[CommonProfile] {

  def isAuthorized(context: WebContext, profiles: java.util.List[CommonProfile]): Boolean = {
    return isAnyAuthorized(context, profiles)
  }

  def isProfileAuthorized(context: WebContext, profile: CommonProfile): Boolean = {
    if (profile == null) {
      false
    } else {
      true // TODO do we need to restrict here?
    }
  }
}