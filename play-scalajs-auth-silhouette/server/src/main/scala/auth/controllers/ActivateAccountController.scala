package auth.controllers

import java.net.URLDecoder
import java.util.UUID
import auth.utils.route.Calls
import auth.views.emails.ActivateAccountEmail
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import play.api.libs.mailer.Email
import play.api.mvc.{Action, AnyContent, Request}
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Activate Account` controller.
 */
class ActivateAccountController @Inject()(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  def send(email: String): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo    = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val msg          = s"We sent another activation email to you at $decodedEmail. " +
      "It might take a few minutes for it to arrive; be sure to check your spam folder."
    val result       = Redirect(Calls.signIn).flashing("info" -> msg)

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.userID).map { authToken =>
          val url      = routes.ActivateAccountController.activate(authToken.id).absoluteURL()
          val emailTxt =
            s"""Hello ${user.name.getOrElse("user")},
               |
               |Please follow the link to confirm and activate your new account: $url""".stripMargin
          mailerClient.send(Email(
            subject = "Activate account",
            from = "Silhouette <noreply@mohiva.com>",
            to = Seq(decodedEmail),
            bodyText = Some(emailTxt),
            bodyHtml = Some(ActivateAccountEmail(user, url).toString())
          ))
          result
        }

      case Some(user) => Future.successful(result) // todo: is this correct?
      case None       => Future.successful(result)
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val invalidActivation = "The link isn't valid anymore! Please sign in to send the activation email again."
    val accountActivated  = "Your account is now activated! Please sign in to use your new account."
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.save(user.copy(activated = true)).map { _ =>
            Redirect(Calls.signIn).flashing("success" -> accountActivated)
          }
        case _                                                                 => Future.successful(
          Redirect(Calls.signIn).flashing("error" -> invalidActivation))
      }
      case None            => Future.successful(
        Redirect(Calls.signIn).flashing("error" -> invalidActivation))
    }
  }
}
