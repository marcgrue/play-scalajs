package auth.controllers

import auth.forms.{TotpForm, TotpSetupForm}
import auth.utils.route.Calls
import auth.views.{HomePage, TotpPage}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `TOTP` controller.
 *
 * Implementation of Time-based One-time Password algorithm for 2-factor authentication.
 */
class TotpController @Inject()(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) with HtmlTag {

  /**
   * Views the `TOTP` page.
   *
   * @return The result to display.
   */
  def view(
    userId: java.util.UUID,
    sharedKey: String,
    rememberMe: Boolean
  ): Action[AnyContent] = UnsecuredAction.async { implicit request =>
    val form = TotpForm.form.fill(TotpForm.Data(userId, sharedKey, rememberMe))
    Future.successful(Ok(TotpPage(CSRF.getToken, form, request.flash)))
  }

  /**
   * Enable TOTP.
   *
   * @return The result to display.
   */
  def enableTotp: Action[AnyContent] = SecuredAction.async { implicit request =>
    val user        = request.identity
    val credentials = totpProvider.createCredentials(user.email.get)
    val totpInfo    = credentials.totpInfo
    val formData    = TotpSetupForm.form.fill(
      TotpSetupForm.Data(totpInfo.sharedKey, totpInfo.scratchCodes, credentials.scratchCodesPlain))
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(HomePage(CSRF.getToken, user, request.flash, totpInfoOpt, Some((formData, credentials))).html)
    }
  }

  /**
   * Disable TOTP.
   *
   * @return The result to display.
   */
  def disableTotp: Action[AnyContent] = SecuredAction.async { implicit request =>
    val user = request.identity
    authInfoRepository.remove[GoogleTotpInfo](user.loginInfo)
    Future(Redirect(Calls.home).flashing("info" -> "2 factor auth disabled successfully!"))
  }

  /**
   * Handles the submitted form with TOTP initial data.
   *
   * @return The result to display.
   */
  def enableTotpSubmit: Action[AnyContent] = SecuredAction.async { implicit request =>
    val user = request.identity
    TotpSetupForm.form.bindFromRequest().fold(
      form => authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
        BadRequest(HomePage(CSRF.getToken, user, request.flash, totpInfoOpt).html)
      },
      data => {
        totpProvider.authenticate(data.sharedKey, data.verificationCode).flatMap {
          case Some(loginInfo: LoginInfo) => {
            authInfoRepository.add[GoogleTotpInfo](user.loginInfo, GoogleTotpInfo(data.sharedKey, data.scratchCodes))
            Future(Redirect(Calls.home).flashing("success" -> "2-factor authentication enabled successfully!"))
          }
          case _                          =>
            Future.successful(Redirect(Calls.home).flashing("error" -> "Invalid verification code!"))
        }.recover {
          case _: ProviderException =>
            Redirect(routes.TotpController
              .view(user.userID, data.sharedKey, request.authenticator.cookieMaxAge.isDefined))
              .flashing("error" -> "Unexpected TOTP exception!")
        }
      }
    )
  }

  /**
   * Handles the submitted form with TOTP verification key.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = UnsecuredAction.async { implicit request =>
    TotpForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(TotpPage(CSRF.getToken, form, request.flash))),
      data => {
        val totpControllerRoute = routes.TotpController.view(data.userID, data.sharedKey, data.rememberMe)
        userService.retrieve(data.userID).flatMap {
          case Some(user) =>
            totpProvider.authenticate(data.sharedKey, data.verificationCode).flatMap {
              case Some(_) => authenticateUser(user, data.rememberMe)
              case _       => Future.successful(Redirect(totpControllerRoute).flashing("error" -> "Invalid verification code!"))
            }.recover {
              case _: ProviderException =>
                Redirect(totpControllerRoute).flashing("error" -> "Unexpected TOTP exception!")
            }
          case None       => Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }
    )
  }
}
