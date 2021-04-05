package auth.views

import auth.controllers.routes
import auth.forms.TotpForm.Data
import play.api.data.Form
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object TotpPage extends SilhouettePage {

  def apply(
    csrfToken: Option[CSRF.Token],
    totpForm0: Form[Data],
    result: Flash
  ): Text.TypedTag[String] = {
    val totpForm = totpForm0.value.get

    page(
      div(
        h3("Two-factor authentication"),

        form(
          method := "post",
          action := routes.TotpController.submit().toString,
          autocomplete := "off",
          input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
          p(input(
            tpe := "text",
            name := "verificationCode",
            placeholder := "Verification code",
            autofocus := true,
            minlength := 6,
            maxlength := 6,
            required := true
          )),
          input(tpe := "hidden", name := "userID", value := totpForm.userID.toString),
          input(tpe := "hidden", name := "sharedKey", value := totpForm.sharedKey),
          input(tpe := "hidden", name := "rememberMe", value := totpForm.rememberMe),

          feedback(result),
          p(button(tpe := "submit", "Verify"))
        )
      )
    )
  }
}