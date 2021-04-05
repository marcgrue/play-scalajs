package auth.controllers

import auth.forms.ForgotPasswordForm
import auth.utils.route.Calls
import auth.views.ForgotPasswordPage
import auth.views.emails.ResetPasswordEmail
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import play.api.libs.mailer.Email
import play.api.mvc.{Action, AnyContent, Request}
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Forgot Password` controller.
 */
class ForgotPasswordController @Inject()(
  components: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(components) with HtmlTag {

  /**
   * Views the `Forgot Password` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(ForgotPasswordPage(CSRF.getToken, request.flash)))
  }

  /**
   * Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    ForgotPasswordForm.form.bindFromRequest().fold(
      _ => Future.successful(BadRequest(ForgotPasswordPage(CSRF.getToken, request.flash))),
      email => {
        val loginInfo      = LoginInfo(CredentialsProvider.ID, email)
        val resetEmailSent = "We have sent you an email with further instructions to reset your password, " +
          "on condition that the address was found in our system. If you do not receive an email within " +
          "the next 5 minutes, then please recheck your entered email address and try it again."
        val result         = Redirect(Calls.signIn).flashing("info" -> resetEmailSent)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            authTokenService.create(user.userID).map { authToken =>
              val url      = routes.ResetPasswordController.view(authToken.id).absoluteURL()
              val emailTxt =
                s"""Hello ${user.name.getOrElse("user")},
                   |
                   |Please follow the link to reset your password: $url""".stripMargin
              mailerClient.send(Email(
                subject = "Reset password",
                from = "Silhouette <noreply@mohiva.com>",
                to = Seq(email),
                bodyText = Some(emailTxt),
                bodyHtml = Some(ResetPasswordEmail(user, url).toString)
              ))
              result
            }

          case Some(user) => Future.successful(result) // todo: is this correct?
          case None       => Future.successful(result)
        }
      }
    )
  }
}
