package auth.controllers

import auth.forms.{SignInForm, TotpForm}
import auth.utils.route.Calls
import auth.views.{ActivateAccountPage, SignInPage, TotpPage}
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Flash, Request}
import play.filters.csrf.CSRF
import playing.scalatags.HtmlTag
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 */
class SignInController @Inject()(
  scc: SilhouetteControllerComponents,
)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) with HtmlTag {

  /**
   * Views the `Sign In` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(SignInPage(CSRF.getToken, request.flash, socialProviderRegistry)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignInForm.form.bindFromRequest().fold(
      _ => Future.successful(BadRequest(
        SignInPage(CSRF.getToken, Flash(Map("error" -> "Error submitting form")), socialProviderRegistry)
      )),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) if !user.activated =>
              Future.successful(Ok(ActivateAccountPage(data.email)))
            case Some(user)                    =>
              authInfoRepository.find[GoogleTotpInfo](user.loginInfo).flatMap {
                case Some(totpInfo) =>
                  val form = TotpForm.form.fill(TotpForm.Data(user.userID, totpInfo.sharedKey, data.rememberMe))
                  Future.successful(Ok(TotpPage(CSRF.getToken, form, request.flash)))
                case _              => authenticateUser(user, data.rememberMe)
              }
            case None                          =>
              Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case _: ProviderException =>
            Redirect(Calls.signIn).flashing("error" -> "Invalid credentials!")
        }
      }
    )
  }
}
