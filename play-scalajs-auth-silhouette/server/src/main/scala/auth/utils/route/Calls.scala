package auth.utils.route

import play.api.mvc.Call
import auth.controllers.routes

/**
 * Defines some common redirect calls used in authentication flow.
 */
object Calls {
  /** @return The URL to redirect to when an authentication succeeds. */
  def home: Call = routes.ApplicationController.index()

  /** @return The URL to redirect to when an authentication fails. */
  def signIn: Call = routes.SignInController.view()
}
