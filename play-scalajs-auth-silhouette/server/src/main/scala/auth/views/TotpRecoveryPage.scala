package auth.views

import auth.controllers.routes
import auth.forms.TotpRecoveryForm.Data
import play.api.data.Form
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object TotpRecoveryPage extends SilhouettePage {

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
          action := routes.TotpRecoveryController.submit().toString,
          autocomplete := "off",
          input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
          p(input(
            tpe := "text",
            name := "recoveryCode",
            placeholder := "Recovery code",
            autofocus := true,
            minlength := 8,
            maxlength := 8,
            required := true
          )),
          input(tpe := "hidden", name := "userID", value := totpForm.userID.toString),
          input(tpe := "hidden", name := "sharedKey", value := totpForm.sharedKey),
          input(tpe := "hidden", name := "rememberMe", value := totpForm.rememberMe),

          feedback(result),
          p(button(tpe := "submit", "Verify"))
        ),

        p("Lost your recovery codes? Please contact support.")
      )
    )
  }
}