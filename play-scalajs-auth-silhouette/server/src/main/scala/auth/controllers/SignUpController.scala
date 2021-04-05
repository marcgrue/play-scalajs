package auth.controllers

import java.util.UUID
import auth.forms.SignUpForm
import auth.models.User
import auth.utils.route.Calls
import auth.views.SignUpPage
import auth.views.emails.{AlreadySignedUpEmail, SignUpEmail}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import play.api.libs.mailer.Email
import play.api.mvc.{Action, AnyContent, Request}
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign Up` controller.
 */
class SignUpController @Inject()(
  components: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(components) with HtmlTag {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(SignUpPage(CSRF.getToken, request.flash)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest().fold(
      _ => Future.successful(BadRequest(SignUpPage(CSRF.getToken, request.flash))),
      data => {
        val msg       = s"You're almost done! We sent an activation mail to ${data.email}. " +
          s"Please follow the instructions in the email to activate your account. " +
          s"If it doesn't arrive, check your spam folder, or try to log in again to " +
          s"send another activation mail."
        val result    = Redirect(routes.SignUpController.view()).flashing("info" -> msg)
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            val url      = Calls.signIn.absoluteURL()
            val emailTxt =
              s"""Hello ${user.name.getOrElse("user")},
                 |
                 |You already have an account registered.
                 |Please follow the link to sign in into your account: $url""".stripMargin
            mailerClient.send(Email(
              subject = "Welcome",
              from = "Silhouette <noreply@mohiva.com>",
              to = Seq(data.email),
              bodyText = Some(emailTxt),
              bodyHtml = Some(AlreadySignedUpEmail(user, url).toString)
            ))
            Future.successful(result)

          case None =>
            val authInfo = passwordHasherRegistry.current.hash(data.password)
            val user     = User(
              userID = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = Some(data.firstName),
              lastName = Some(data.lastName),
              fullName = Some(data.firstName + " " + data.lastName),
              email = Some(data.email),
              avatarURL = None,
              activated = false
            )
            for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.save(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(user.userID)
            } yield {
              val url      = routes.ActivateAccountController.activate(authToken.id).absoluteURL()
              val emailTxt =
                s"""Hello ${user.name.getOrElse("user")},
                   |
                   |Please follow the link to confirm and activate your new account: $url""".stripMargin
              mailerClient.send(Email(
                subject = "Welcome",
                from = "Silhouette <noreply@mohiva.com>",
                to = Seq(data.email),
                bodyText = Some(emailTxt),
                bodyHtml = Some(SignUpEmail(user, url).toString)
              ))
              eventBus.publish(SignUpEvent(user, request))
              result
            }
        }
      }
    )
  }
}
