package auth.controllers

import auth.utils.route.Calls
import auth.views.HomePage
import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.impl.providers.GoogleTotpInfo
import javax.inject.Inject
import play.api.mvc._
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.ExecutionContext

/**
 * The basic application controller.
 */
class ApplicationController @Inject()(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) with HtmlTag {

  /**
   * Handles the index action.
   *
   * Calls onNotAuthenticated in CustomSecuredErrorHandler if user is not authenticated
   * and then re-directs to SignIn page.
   *
   * Calls onNotAuthorized in CustomSecuredErrorHandler if user is authenticated
   * but not authorized and then re-directs to SignIn page.
   *
   * @return The result to display.
   */
  def index: Action[AnyContent] = SecuredAction.async { implicit request: SecuredRequest[EnvType, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(HomePage(CSRF.getToken, request.identity, request.flash, totpInfoOpt).html)
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut: Action[AnyContent] = SecuredAction.async { implicit request: SecuredRequest[EnvType, AnyContent] =>
    val result = Redirect(Calls.home)
    eventBus.publish(LogoutEvent(request.identity, request))
    authenticatorService.discard(request.authenticator, result)
  }
}
