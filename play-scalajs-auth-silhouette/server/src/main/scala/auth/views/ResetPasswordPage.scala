package auth.views

import java.util.UUID
import auth.controllers.routes
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object ResetPasswordPage extends SilhouettePage {

  def apply(
    csrfToken: Option[CSRF.Token],
    token: UUID,
    result: Flash
  ): Text.TypedTag[String] =
    page(
      h3("Reset password"),
      p("Strong passwords include numbers, letters and punctuation marks."),
      form(
        method := "post",
        action := routes.ResetPasswordController.submit(token).toString,
        autocomplete := "off",
        input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
        p(input(tpe := "password", name := "password", placeholder := "Password", autofocus := true)),
        feedback(result),
        p(button(tpe := "submit", "Reset"))
      )
    )
}