package auth.views

import auth.controllers.routes
import scalatags.Text
import scalatags.Text.all._
import views.html.helper

object ActivateAccountPage extends SilhouettePage {

  def apply(email: String): Text.TypedTag[String] = {
    val url = routes.ActivateAccountController.send(helper.urlEncode(email)).toString
    page(
      div(
        h3("Activate account"),

        p("You can't log in yet. We previously sent an activation email to you at:"),
        p(b(email)),
        p("Please follow the instructions in that email to activate your account."),
        p("Click ", a(href := url, "here"), " to send the activation email again."),
      )
    )
  }
}