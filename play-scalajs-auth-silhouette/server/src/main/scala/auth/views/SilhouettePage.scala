package auth.views

import auth.controllers.routes
import controllers.routes.Assets
import play.api.mvc.Flash
import scalatags.Text
import scalatags.Text.all._

/**
 * Page template and shared elements for various authentication pages.
 */
trait SilhouettePage {

  def page(elements: Text.TypedTag[String]*): Text.TypedTag[String] =
    html(
      head(
        link(
          rel := "stylesheet",
          href := Assets.versioned("stylesheets/main.css").url
        )
      ),
      body(
        h1(
          display.`inline-flex`,
          a(href := "https://www.silhouette.rocks",
            img(src := Assets.versioned(s"images/silhouette-logo.png").url),
          ),
          div(
            paddingTop := 15,
            paddingLeft := 20,
            "Authentication with Silhouette",
          ),
          marginBottom := 10
        ),
        elements
      )
    )

  def feedback(result: Flash): Text.TypedTag[String] =
    div(
      Seq(
        result.get("error").map(e => p(s"Error! $e", color.red)),
        result.get("info").map(e => p(e, color.blue)),
        result.get("success").map(e => p(e, color.green))
      ).flatten[Text.Modifier]
    )

  lazy val alreadyAMember: Text.TypedTag[String] =
    p("Already a member? ", a(href := routes.SignInController.view().toString, "Sign in now"))
}