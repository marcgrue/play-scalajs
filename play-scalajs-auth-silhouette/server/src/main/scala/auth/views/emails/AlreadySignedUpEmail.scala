package auth.views.emails

import auth.models.User
import scalatags.Text
import scalatags.Text.all._

object AlreadySignedUpEmail {

  def apply(user: User, url: String): Text.TypedTag[String] = {
    html(
      body(
        p(s"Hello ${user.name.getOrElse("user")},"),
        p(
          "You already have an account registered. Please follow ",
          a(href := url, "this link"),
          " to sign in into your account."
        )
      )
    )
  }
}