package auth.controllers

import auth.forms.ChangePasswordForm
import auth.utils.auth.{DefaultEnv, WithProvider}
import auth.views.ChangePasswordPage
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import play.api.mvc._
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Change Password` controller.
 */
class ChangePasswordController @Inject()(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) with HtmlTag {

  /**
   * Views the `Change Password` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)) {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      Ok(ChangePasswordPage(CSRF.getToken, request.identity, request.flash))
  }

  /**
   * Changes the password.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)).async {
    val passwordChanged        = "Your password has been changed."
    val currentPasswordInvalid = "The entered password is invalid. Please enter the correct password!"
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      ChangePasswordForm.form.bindFromRequest().fold(
        _ => Future.successful(BadRequest(ChangePasswordPage(CSRF.getToken, request.identity, request.flash))),
        password => {
          val (currentPassword, newPassword) = password
          val credentials                    = Credentials(request.identity.email.getOrElse(""), currentPassword)
          credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
            val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
              Redirect(routes.ChangePasswordController.view()).flashing("success" -> passwordChanged)
            }
          }.recover {
            case _: ProviderException =>
              Redirect(routes.ChangePasswordController.view()).flashing("error" -> currentPasswordInvalid)
          }
        }
      )
  }
}
