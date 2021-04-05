package auth.views

import auth.controllers.routes
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object SignUpPage extends SilhouettePage {

  def apply(csrfToken: Option[CSRF.Token], result: Flash): Text.TypedTag[String] =
    page(
      h3("Sign up for a new account"),
      form(
        method := "post",
        action := routes.SignUpController.submit().toString,
        input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
        p(input(tpe := "text", name := "firstName", placeholder := "First name", autocomplete := "first-name", autofocus := true)),
        p(input(tpe := "text", name := "lastName", placeholder := "Last name")),
        p(input(tpe := "text", name := "email", placeholder := "Email")),
        p(input(tpe := "password", name := "password", placeholder := "Password")),
        feedback(result),
        p(button(tpe := "submit", "Sign up")),
        alreadyAMember
      )
    )
}