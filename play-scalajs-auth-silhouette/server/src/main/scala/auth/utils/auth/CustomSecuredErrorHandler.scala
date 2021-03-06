package auth.utils.auth

import javax.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Call, RequestHeader, Result}
import play.api.mvc.Results._
import auth.utils.route.Calls
import scala.concurrent.Future

/**
 * Custom secured error handler.
 *
 * @param messagesApi The Play messages API.
 */
class CustomSecuredErrorHandler @Inject() (val messagesApi: MessagesApi) extends SecuredErrorHandler {

  /**
   * Called when a user is not authenticated.
   *
   * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
   *
   * @param request The request header.
   * @return The result to send to the client.
   */
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
    Future.successful(Redirect(Calls.signIn))
  }

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
    Future.successful(Redirect(Calls.signIn).flashing("error" -> "Access denied!"))
  }
}
