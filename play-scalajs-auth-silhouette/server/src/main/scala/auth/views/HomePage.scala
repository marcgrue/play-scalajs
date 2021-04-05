package auth.views

import auth.controllers.routes
import auth.forms.TotpSetupForm
import auth.models.User
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.{GoogleTotpCredentials, GoogleTotpInfo}
import controllers.routes.Assets
import play.api.data.Form
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

case class HomePage(
  csrfToken: Option[CSRF.Token],
  user: User,
  result: Flash,
  totpInfoOpt: Option[GoogleTotpInfo],
  totpDataOpt: Option[(Form[TotpSetupForm.Data], GoogleTotpCredentials)] = None
) extends SilhouettePage {

  def html: Text.TypedTag[String] = {
    val firstName = user.firstName.fold("")(f => s" $f")
    page(
      h3(s"Welcome$firstName, you are now signed in!"),
      table(cls := "homeTable",
        tr(
          td(
            user.avatarURL.fold(
              img(src := Assets.versioned("images/silhouette.png").url)
            )(url =>
              img(src := url)
            )
          ),
          td(
            p(user.name),
            p(user.email)
          ),
        )
      ),
      p(
        a(href := routes.ApplicationController.signOut().toString, "Sign out"), " | ",
        a(href := routes.ChangePasswordController.view().toString, "Change password")
      ),

      if (totpInfoOpt.isEmpty)
        twoFactorNotEnabled
      else
        twoFactorEnabled,

      br,
      p(a(href := "/public", "Public page"), " | ", a(href := "/secret", "Secret page"))
    )
  }


  def twoFactorNotEnabled: Text.TypedTag[String] = {
    val gaMac    = a(href := "https://apps.apple.com/us/app/google-authenticator/id388497605", "Mac")
    val gaWin    = a(href := "https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en", "Android")
    val elements = totpDataOpt match {
      case Some((totpForm0, credentials)) =>
        val totpForm = totpForm0.value.get
        Seq(
          p("Shared key as QR:"),
          img(src := credentials.qrUrl),
          p("Recovery tokens:"),
          ul(credentials.scratchCodesPlain.map(c => li(c))),

          form(
            method := "post",
            action := routes.TotpController.enableTotpSubmit().toString,
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
            input(tpe := "hidden", name := s"sharedKey", value := totpForm.sharedKey),
            totpForm.scratchCodes.zipWithIndex.flatMap {
              case (PasswordInfo(hasher, password, salt), i) => Seq(
                input(tpe := "hidden", name := s"scrachCodes[$i].hasher", value := hasher),
                input(tpe := "hidden", name := s"scrachCodes[$i].password", value := password),
                input(tpe := "hidden", name := s"scrachCodes[$i].salt", value := salt.getOrElse("")),
              )
            },
            feedback(result),
            p(button(tpe := "submit", "Verify")),

            i("To get the verification code, you can scan the QR with the " +
              "Google Authenticator app (", gaMac, ", ", gaWin, ")")
          )
        )

      case None =>
        Seq(
          a(href := routes.TotpController.enableTotp().toString, "Enable 2-factor authentication")
        )
    }

    div(
      h3("2-factor authentication not enabled"),
      elements
    )
  }

  def twoFactorEnabled: Text.TypedTag[String] =
    div(
      h3("2-factor authentication enabled"),
      a(href := routes.TotpController.disableTotp().toString, "Disable 2-factor authentication")
    )
}