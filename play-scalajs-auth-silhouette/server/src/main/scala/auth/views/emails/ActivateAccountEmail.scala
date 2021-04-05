package auth.views.emails

import auth.models.User
import scalatags.Text
import scalatags.Text.all._

object ActivateAccountEmail {

  def apply(user: User, url: String): Text.TypedTag[String] = {
    html(
      body(
        p(s"Hello ${user.name.getOrElse("user")},"),
        p("Please follow ", a(href := url, "this link"), " to confirm and activate your new account.")
      )
    )
  }
}