package auth.views

import auth.controllers.routes
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object ForgotPasswordPage extends SilhouettePage {

  def apply(csrfToken: Option[CSRF.Token], result: Flash): Text.TypedTag[String] =
    page(
      h3("Forgot password"),
      p("Please enter your email address and we will send you an email with " +
        "further instructions to reset your password."),
      form(
        method := "post",
        action := routes.ForgotPasswordController.submit().toString,
        input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
        p(input(tpe := "text", name := "email", placeholder := "Email", autofocus := true)),
        feedback(result),
        p(button(tpe := "submit", "Send"))
      )
    )
}