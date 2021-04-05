package auth.views

import auth.controllers.routes
import com.mohiva.play.silhouette.impl.providers.{SocialProvider, SocialProviderRegistry}
import controllers.routes.Assets
import play.api.mvc.Flash
import play.filters.csrf.CSRF
import scalatags.Text
import scalatags.Text.all._

object SignInPage extends SilhouettePage {

  def apply(
    csrfToken: Option[CSRF.Token],
    result: Flash,
    socialProviders: SocialProviderRegistry
  ): Text.TypedTag[String] =
    page(
      h3("Sign in with your credentials"),
      form(
        method := "post",
        action := routes.SignInController.submit().toString,
        input(tpe := "hidden", name := "csrfToken", value := csrfToken.get.value),
        p(input(tpe := "text", name := "email", placeholder := "Email", autofocus := true)),
        p(input(tpe := "password", name := "password", placeholder := "Password")),
        feedback(result),
        p(
          input(tpe := "checkbox", name := "rememberMe", value := true, checked := true),
          " Remember my login on this computer"
        ),
        p(button(tpe := "submit", "Sign in"))
      ),

      div(
        p("Or sign in with social account:"),
        socialProviders.providers.map(getSocialLogin)
      ),

      p(
        "Not a member? ", a(href := routes.SignUpController.view().toString, "Sign up now"), " | ",
        a(href := routes.ForgotPasswordController.view().toString, "Forgot your password?")
      ),
      br,
      p(a(href := "/public", "Public page"))
    )

  val providerNames = Map(
    "google" -> "Google",
    "facebook" -> "Facebook",
    "twitter" -> "Twitter",
    "vk" -> "VK",
    "xing" -> "Xing",
    "yahoo" -> "Yahoo",
  )

  def getSocialLogin(provider: SocialProvider): Text.TypedTag[String] = {
    val url = routes.SocialAuthController.authenticate(provider.id).toString
    val pId = provider.id
    a(href := url, cls := s"provider $pId", title := providerNames(pId),
      img(src := Assets.versioned(s"images/providers/$pId.png").url)
    )
  }
}