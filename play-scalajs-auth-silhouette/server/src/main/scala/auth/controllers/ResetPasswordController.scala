package auth.controllers

import java.util.UUID
import auth.forms.ResetPasswordForm
import auth.utils.route.Calls
import auth.views.ResetPasswordPage
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Request}
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Reset Password` controller.
 */
class ResetPasswordController @Inject()(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) with HtmlTag {

  /**
   * Views the `Reset Password` page.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def view(token: UUID): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.validate(token).map {
      case Some(_) => Ok(ResetPasswordPage(CSRF.getToken, token, request.flash))
      case None    => Redirect(Calls.signIn)
        .flashing("error" -> "The link isn't valid anymore! Please request a new link to reset your password.")
    }
  }

  /**
   * Resets the password.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def submit(token: UUID): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val invalidResetLink = "The link isn't valid anymore! Please request a new link to reset your password."
    val passwordReset    = "We have reset your password. You can now sign in with your credentials."
    authTokenService.validate(token).flatMap {
      case Some(authToken) =>
        ResetPasswordForm.form.bindFromRequest().fold(
          _ => Future.successful(BadRequest(ResetPasswordPage(CSRF.getToken, token, request.flash))),
          password => userService.retrieve(authToken.userID).flatMap {
            case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
              val passwordInfo = passwordHasherRegistry.current.hash(password)
              authInfoRepository.update[PasswordInfo](user.loginInfo, passwordInfo).map { _ =>
                Redirect(Calls.signIn).flashing("success" -> passwordReset)
              }
            case _                                                                 => Future.successful(
              Redirect(Calls.signIn).flashing("error" -> invalidResetLink))
          }
        )
      case None            => Future.successful(
        Redirect(Calls.signIn).flashing("error" -> invalidResetLink))
    }
  }
}
