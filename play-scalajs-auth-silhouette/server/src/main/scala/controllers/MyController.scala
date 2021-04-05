package controllers

import auth.controllers.{SilhouetteController, SilhouetteControllerComponents}
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.impl.providers.GoogleTotpInfo
import javax.inject.Inject
import mydomain.server.pages.{PublicPage, SecretPage}
import play.api.mvc._
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * Example of a controller in your domain
 */
class MyController @Inject()(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) with HtmlTag {

  /**
   * Example of secret page action requiring the user to be authenticated.
   *
   * Calls onNotAuthenticated in CustomSecuredErrorHandler if user is not authenticated
   * and then re-directs to SignIn page.
   *
   * Calls onNotAuthorized in CustomSecuredErrorHandler if user is authenticated
   * but not authorized and then re-directs to SignIn page.
   *
   * @return Secret page requiring authentication
   */
  def secret: Action[AnyContent] = SecuredAction.async { implicit request: SecuredRequest[EnvType, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(SecretPage(request)())
    }
  }

  /**
   * Example of public page action being aware if user is authenticated or not.
   *
   * @return Public page aware of user authentication
   */
  def public: Action[AnyContent] = UserAwareAction.async { implicit request =>
    Future.successful(Ok(PublicPage(request)(request.identity.nonEmpty)))
  }
}
