package auth.views

import auth.controllers.routes
import auth.models.User
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object ChangePasswordPage extends SilhouettePage {

  def apply(
    csrfToken: Option[CSRF.Token],
    user: User,
    result: Flash
  ): Text.TypedTag[String] =
    page(
      h3("Change password"),
      p("Strong passwords include numbers, letters and punctuation marks."),
      form(
        method := "post",
        action := routes.ChangePasswordController.submit().toString,
        autocomplete := "off",
        input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
        p(input(tpe := "password", name := "password", placeholder := "Current password", autofocus := true)),
        p(input(tpe := "password", name := "password", placeholder := "New password")),
        feedback(result),
        p(button(tpe := "submit", "Reset"))
      )
    )
}