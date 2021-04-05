package mydomain.client

import org.scalajs.dom.document
import scalatags.JsDom.all._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("PublicClient")
object PublicClient {

  @JSExport
  def load(authenticated: Boolean = false): Unit = {
    val secretOrSignIn = if (authenticated)
      a(href := "/secret", "See the answer")
    else
      a(href := "/signIn", "Sign in to see the answer")

    document.body.appendChild(
      div(
        h3("Public page"),
        p(mydomain.shared.Dialogue.question, secretOrSignIn),
        if(!authenticated) p(a(href := "/signIn", "Sign in")) else ()
      ).render
    )
  }
}
