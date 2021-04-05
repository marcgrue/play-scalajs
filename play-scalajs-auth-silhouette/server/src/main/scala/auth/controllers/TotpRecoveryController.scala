package auth.controllers

import java.util.UUID
import auth.forms.TotpRecoveryForm
import auth.views.TotpRecoveryPage
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
 */
class TotpRecoveryController @Inject()(
  scc: SilhouetteControllerComponents,
)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) with HtmlTag {

  /**
   * Views the TOTP recovery page.
   *
   * @param userID     the user ID.
   * @param sharedKey  the shared key associated to the user.
   * @param rememberMe the remember me flag.
   * @return The result to display.
   */
  def view(
    userID: UUID,
    sharedKey: String,
    rememberMe: Boolean
  ): Action[AnyContent] = UnsecuredAction.async { implicit request =>
    val form = TotpRecoveryForm.form.fill(TotpRecoveryForm.Data(userID, sharedKey, rememberMe))
    Future.successful(Ok(TotpRecoveryPage(CSRF.getToken, form, request.flash)))
  }

  /**
   * Handles the submitted form with TOTP verification key.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = UnsecuredAction.async { implicit request =>
    TotpRecoveryForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(TotpRecoveryPage(CSRF.getToken, form, request.flash))),
      data => {
        val totpRecoveryControllerRoute = routes.TotpRecoveryController.view(data.userID, data.sharedKey, data.rememberMe)
        userService.retrieve(data.userID).flatMap {
          case Some(user) => {
            authInfoRepository.find[GoogleTotpInfo](user.loginInfo).flatMap {
              case Some(totpInfo) =>
                totpProvider.authenticate(totpInfo, data.recoveryCode).flatMap {
                  case Some(updated) => {
                    authInfoRepository.update[GoogleTotpInfo](user.loginInfo, updated._2)
                    authenticateUser(user, data.rememberMe)
                  }
                  case _             => Future.successful(
                    Redirect(totpRecoveryControllerRoute).flashing("error" -> "Invalid recovery code!"))
                }.recover {
                  case _: ProviderException =>
                    Redirect(totpRecoveryControllerRoute).flashing("error" -> "Unexpected TOTP exception!")
                }
              case _              => Future.successful(
                Redirect(totpRecoveryControllerRoute).flashing("error" -> "Unexpected TOTP exception!"))
            }
          }
          case None       => Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }
    )
  }
}
